import { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Upload,
  Send,
  Menu,
  X,
  FileText,
  Music,
  Video,
  Trash2,
} from "lucide-react";
import { fileService } from "@/lib/api";

interface UploadedFile {
  id: string;
  name: string;
  type: "pdf" | "audio" | "video";
  size: string;
  summary?: string;
  extractedText?: string;
}

export default function AppPage() {
  const navigate = useNavigate();
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");
  const [files, setFiles] = useState<UploadedFile[]>([]);
  const chatContainerRef = useRef<HTMLDivElement>(null);
  const [messages, setMessages] = useState<{ role: string; content: string }[]>([
    {
      role: "assistant",
      content: "Hello! Upload a file and ask me questions about it.",
    },
  ]);

  const [input, setInput] = useState("");
  const [selectedFile, setSelectedFile] = useState<UploadedFile | null>(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const mediaRef = useRef<HTMLVideoElement | HTMLAudioElement | null>(null);

  const seekTo = (timeStr: string) => {
    if (!mediaRef.current) return;
    
    // Parse [mm:ss-mm:ss] or [mm:ss]
    const timeMatch = timeStr.match(/\[(\d{2}:\d{2})(?:-(\d{2}:\d{2}))?\]/);
    if (!timeMatch) return;

    const startStr = timeMatch[1];
    const endStr = timeMatch[2];

    const toSeconds = (str: string) => {
      const [m, s] = str.split(":").map(Number);
      return m * 60 + s;
    };

    const startSeconds = toSeconds(startStr);
    mediaRef.current.currentTime = startSeconds;
    mediaRef.current.play();

    // If we have an end time, set a timer to pause
    if (endStr) {
      const endSeconds = toSeconds(endStr);
      const duration = (endSeconds - startSeconds) * 1000;
      
      // Clear any existing timers (simple way)
      if ((window as any)._stopTimer) clearTimeout((window as any)._stopTimer);
      
      (window as any)._stopTimer = setTimeout(() => {
        if (mediaRef.current) mediaRef.current.pause();
      }, duration + 500); // Add 500ms buffer for natural feel
    }
  };

  const parseTimestamps = (text: string) => {
    const lines = text.split("\n");
    return lines
      .filter(line => line.trim().startsWith("["))
      .map(line => {
        const timeMatch = line.match(/^\[\d{2}:\d{2}-\d{2}:\d{2}\]/);
        if (!timeMatch) {
            // Fallback for old format
            const oldMatch = line.match(/^\[\d{2}:\d{2}\]/);
            if(!oldMatch) return null;
            return { time: oldMatch[0], content: line.replace(oldMatch[0], "").trim() };
        }
        const time = timeMatch[0];
        const content = line.replace(time, "").trim();
        return { time, content };
      })
      .filter(Boolean);
  };

  const MessageContent = ({ content, role }: { content: string; role: string }) => {
    const parts = content.split(/(\[\d{2}:\d{2}(?:-\d{2}:\d{2})?\])/g);
    const hasTimestamp = content.match(/\[\d{2}:\d{2}(?:-\d{2}:\d{2})?\]/);

    return (
      <div className="flex flex-col gap-2">
        <p>
          {parts.map((part, i) => {
            if (part.match(/\[\d{2}:\d{2}(?:-\d{2}:\d{2})?\]/)) {
              return (
                <button
                  key={i}
                  onClick={() => seekTo(part)}
                  className="text-blue-500 font-mono font-bold hover:underline bg-blue-100 px-1 rounded"
                >
                  {part}
                </button>
              );
            }
            return part;
          })}
        </p>
        {role === "assistant" && hasTimestamp && (
          <Button
            size="sm"
            variant="outline"
            className="w-fit text-[10px] h-7 gap-1 bg-white hover:bg-blue-50 text-blue-600 border-blue-200"
            onClick={() => seekTo(hasTimestamp[0])}
          >
            <Music className="w-3 h-3" />
            Play Relevant Portion
          </Button>
        )}
      </div>
    );
  };

  useEffect(() => {
    const fetchFiles = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/files", {
          headers: {
            "Authorization": `Bearer ${localStorage.getItem("token")}`
          }
        });
        if (response.ok) {
          const data = await response.json();
          const formattedFiles = data.map((f: any) => ({
            id: f.fileId.toString(),
            name: f.fileName,
            type: f.fileType.toLowerCase() as "pdf" | "audio" | "video",
            size: (f.fileSize / 1024 / 1024).toFixed(1) + " MB",
            summary: f.summary,
            extractedText: f.extractedText
          }));
          setFiles(formattedFiles);
          if (formattedFiles.length > 0) setSelectedFile(formattedFiles[0]);
        }
      } catch (err) {
        console.error("Failed to load files", err);
      }
    };
    fetchFiles();
  }, []);

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [messages]);

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || !selectedFile) return;

    const userMessage = { role: "user", content: input };
    setMessages((prev) => [...prev, userMessage]);
    setInput("");

    try {
      const response = await fetch("http://localhost:8080/api/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify({
          fileId: selectedFile.id,
          message: input,
        }),
      });

      if (!response.ok) throw new Error("AI failed to respond");

      const data = await response.json();
      setMessages((prev) => [...prev, { role: "assistant", content: data.answer }]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        { role: "assistant", content: "I'm sorry, I couldn't process that question right now." },
      ]);
    }
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    setError("");
    try {
      const data = await fileService.upload(file);
      const type = file.type.includes("pdf") || file.name.endsWith(".pdf") ? "pdf" : file.type.includes("video") || file.name.endsWith(".mp4") ? "video" : "audio";

      const newFile: UploadedFile = {
        id: data.fileId.toString(),
        name: file.name,
        type: type as "pdf" | "audio" | "video",
        size: `${(file.size / 1024 / 1024).toFixed(1)} MB`,
        summary: data.summary,
        extractedText: data.extractedText
      };

      setFiles([newFile, ...files]);
      setSelectedFile(newFile);
      setMessages(prev => [...prev, { role: 'assistant', content: `Successfully analyzed ${file.name}. You can now explore the summary or ask questions.` }]);
    } catch (err) {
      setError("Failed to upload file. Please try again.");
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteFile = async (id: string) => {
    try {
      await fileService.delete(id);
      setFiles(files.filter((f) => f.id !== id));
      if (selectedFile?.id === id) {
        const remainingFiles = files.filter((f) => f.id !== id);
        setSelectedFile(remainingFiles.length > 0 ? remainingFiles[0] : null);
      }
    } catch (err) {
      console.error("Failed to delete file", err);
    }
  };

  const handleSignOut = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <div className="min-h-screen bg-white">
      <header className="sticky top-0 z-40 border-b border-border bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <Link to="/" className="font-bold text-lg">InsightAI</Link>
          <div className="hidden sm:flex items-center gap-4">
            <Button variant="ghost" size="sm" onClick={handleSignOut}>Sign Out</Button>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-1">
            <div className="bg-white border border-border rounded-lg p-6">
              <h2 className="text-lg font-semibold mb-4">Your Files</h2>
              <label className="block mb-6">
                <div className={`border-2 border-dashed border-border rounded-lg p-6 text-center hover:bg-gray-50 cursor-pointer transition-all ${uploading ? 'opacity-50 grayscale' : ''}`}>
                  <Upload className="w-6 h-6 mx-auto mb-2 text-gray-400" />
                  <p className="text-sm font-medium">{uploading ? "Analyzing..." : "Upload Files"}</p>
                  <input type="file" className="hidden" accept=".pdf,.mp3,.wav,.mp4" onChange={handleFileUpload} disabled={uploading} />
                </div>
              </label>
              {error && <p className="text-xs text-red-500 mb-4">{error}</p>}
              <div className="space-y-2">
                {files.map((file) => (
                  <div key={file.id} onClick={() => setSelectedFile(file)} className={`p-3 rounded border cursor-pointer ${selectedFile?.id === file.id ? "border-primary bg-blue-50" : "border-border"}`}>
                    <div className="flex items-center gap-2">
                      {file.type === "pdf" ? <FileText className="w-4 h-4 text-red-500" /> : file.type === "audio" ? <Music className="w-4 h-4 text-purple-500" /> : <Video className="w-4 h-4 text-pink-500" />}
                      <span className="text-sm font-medium truncate flex-1">{file.name}</span>
                      <button onClick={(e) => { e.stopPropagation(); handleDeleteFile(file.id); }} className="text-gray-400 hover:text-red-500"><Trash2 className="w-4 h-4" /></button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Hidden Media Player (logic only) */}
            {selectedFile && selectedFile.type !== "pdf" && (
              <div className="opacity-0 pointer-events-none absolute h-0 w-0 overflow-hidden" aria-hidden="true">
                {selectedFile.type === "video" ? (
                  <video ref={mediaRef as any} src={`http://localhost:8080/api/files/view/${selectedFile.id}`} preload="auto" />
                ) : (
                  <audio ref={mediaRef as any} src={`http://localhost:8080/api/files/view/${selectedFile.id}`} preload="auto" />
                )}
              </div>
            )}

            <Tabs defaultValue="chat" className="w-full">
              <TabsList className="grid w-full grid-cols-3">
                <TabsTrigger value="chat">Chat</TabsTrigger>
                <TabsTrigger value="summary">Summary</TabsTrigger>
                <TabsTrigger value="timestamps">Timestamps</TabsTrigger>
              </TabsList>

              <TabsContent value="chat">
                <div className="bg-white border border-border rounded-lg p-6 h-[400px] flex flex-col">
                  <div ref={chatContainerRef} className="flex-1 overflow-y-auto mb-4 space-y-4 pr-2">
                    {messages.map((msg, idx) => (
                      <div key={idx} className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}>
                        <div className={`max-w-[85%] px-4 py-2 rounded text-sm ${msg.role === "user" ? "bg-blue-600 text-white" : "bg-gray-100"}`}>
                          <MessageContent content={msg.content} role={msg.role} />
                        </div>
                      </div>
                    ))}
                  </div>
                  <form onSubmit={handleSendMessage} className="flex gap-2">
                    <Input placeholder="Ask about this file..." value={input} onChange={(e) => setInput(e.target.value)} className="h-10 flex-1" />
                    <Button type="submit" size="icon" className="h-10 w-10"><Send className="w-4 h-4" /></Button>
                  </form>
                </div>
              </TabsContent>

              <TabsContent value="summary">
                <div className="bg-white border border-border rounded-lg p-6 min-h-[400px]">
                  <h3 className="font-semibold mb-4">AI Summary</h3>
                  <div className="text-muted-foreground text-sm whitespace-pre-line leading-relaxed mb-8">
                    {selectedFile?.summary || "Summary will be generated upon upload."}
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="p-4 bg-gray-50 rounded border border-border">
                      <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-2">Metadata</p>
                      <p className="text-xs"><strong>Format:</strong> {selectedFile?.type.toUpperCase()}</p>
                      <p className="text-xs"><strong>Size:</strong> {selectedFile?.size}</p>
                    </div>
                    <div className="p-4 bg-blue-50 rounded border border-blue-100">
                      <p className="text-[10px] font-bold text-blue-500 uppercase tracking-widest mb-2">AI Analysis</p>
                      <p className="text-xs text-blue-700 font-medium">Ready for Q&A</p>
                    </div>
                  </div>
                </div>
              </TabsContent>

              <TabsContent value="timestamps">
                <div className="bg-white border border-border rounded-lg p-6 min-h-[400px]">
                  <h3 className="font-semibold mb-4 text-blue-600">Media Timeline</h3>
                  <div className="space-y-2 max-h-[500px] overflow-y-auto pr-2 custom-scrollbar">
                    {selectedFile && selectedFile.extractedText ? (
                      parseTimestamps(selectedFile.extractedText).map((item: any, idx: number) => (
                        <div key={idx} className="flex items-start gap-4 p-3 rounded-lg border border-transparent hover:border-blue-200 hover:bg-blue-50/50 transition-all">
                          <button onClick={() => seekTo(item.time)} className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs font-mono font-bold hover:bg-blue-600 hover:text-white transition-colors">
                            {item.time}
                          </button>
                          <p className="text-sm text-gray-600 flex-1 leading-relaxed">{item.content}</p>
                        </div>
                      ))
                    ) : (
                      <p className="text-sm text-gray-400 italic">No timeline data available for this file.</p>
                    )}
                  </div>
                </div>
              </TabsContent>
            </Tabs>
          </div>
        </div>
      </div>
    </div>
  );
}
