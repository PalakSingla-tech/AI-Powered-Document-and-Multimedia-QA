import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Upload,
  MessageSquare,
  Zap,
  Play,
  Clock,
  FileText,
  Headphones,
  Video,
} from "lucide-react";

export default function Index() {
  return (
    <div className="min-h-screen bg-white text-foreground">
      {/* Navigation */}
      <nav className="sticky top-0 z-50 bg-white border-b border-border">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2 font-bold text-lg">
            <span>InsightAI</span>
          </div>
          <div className="flex items-center gap-4">
            <Link to="/login">
              <Button variant="ghost" size="sm">
                Sign In
              </Button>
            </Link>
            <Link to="/signup">
              <Button size="sm">Get Started</Button>
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto">
        <div className="text-center mb-16">
          <h1 className="text-5xl sm:text-6xl font-bold mb-6">
            Chat with Your Content
          </h1>
          <p className="text-xl text-muted-foreground max-w-2xl mx-auto mb-8">
            Upload PDFs, audio, and videos. Ask questions, get summaries, and jump to specific moments instantly.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/signup">
              <Button size="lg">
                Create Account
              </Button>
            </Link>
          </div>
        </div>

        {/* Features Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-20">
          <div className="p-6 border border-border rounded-lg">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
              <Upload className="w-6 h-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Upload Files</h3>
            <p className="text-muted-foreground text-sm">
              Support for PDFs, audio, and video files.
            </p>
          </div>

          <div className="p-6 border border-border rounded-lg">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
              <MessageSquare className="w-6 h-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold mb-2">AI Chat</h3>
            <p className="text-muted-foreground text-sm">
              Ask questions about your content instantly.
            </p>
          </div>

          <div className="p-6 border border-border rounded-lg">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
              <Zap className="w-6 h-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Summaries</h3>
            <p className="text-muted-foreground text-sm">
              Get auto-generated summaries instantly.
            </p>
          </div>

          <div className="p-6 border border-border rounded-lg">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
              <Clock className="w-6 h-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Timestamps</h3>
            <p className="text-muted-foreground text-sm">
              Extract timestamps for specific topics.
            </p>
          </div>

          <div className="p-6 border border-border rounded-lg">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
              <Play className="w-6 h-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold mb-2">Play Anywhere</h3>
            <p className="text-muted-foreground text-sm">
              Jump to any moment with one click.
            </p>
          </div>

          <div className="p-6 border border-border rounded-lg">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
              <FileText className="w-6 h-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold mb-2">All Formats</h3>
            <p className="text-muted-foreground text-sm">
              PDF, MP3, WAV, MP4, WebM and more.
            </p>
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="py-16 px-4 sm:px-6 lg:px-8 bg-gray-50 border-t border-border">
        <div className="max-w-6xl mx-auto">
          <h2 className="text-3xl font-bold text-center mb-12">How It Works</h2>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            {[
              { step: "1", title: "Upload", description: "Add your PDF, audio, or video" },
              { step: "2", title: "Ask", description: "Chat with AI about content" },
              { step: "3", title: "Explore", description: "Get summaries & timestamps" },
              { step: "4", title: "Navigate", description: "Jump to specific sections" },
            ].map((item) => (
              <div key={item.step} className="text-center">
                <div className="w-12 h-12 bg-blue-600 text-white rounded-full flex items-center justify-center font-bold text-lg mx-auto mb-4">
                  {item.step}
                </div>
                <h3 className="font-semibold mb-1">{item.title}</h3>
                <p className="text-sm text-muted-foreground">{item.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto text-center">
        <h2 className="text-3xl font-bold mb-6">Ready to Get Started?</h2>
        <p className="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">
          Create an account to start transforming your content.
        </p>
        <Link to="/signup">
          <Button size="lg">Create Free Account</Button>
        </Link>
      </section>

      {/* Footer */}
      <footer className="border-t border-border bg-gray-50 py-8 px-4 sm:px-6 lg:px-8 text-center text-sm text-muted-foreground">
        <p>© 2024 MediaAI. All rights reserved.</p>
      </footer>
    </div>
  );
}
