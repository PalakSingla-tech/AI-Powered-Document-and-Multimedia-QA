import { useLocation } from "react-router-dom";
import { Link } from "react-router-dom";
import { useEffect } from "react";
import { Button } from "@/components/ui/button";

const NotFound = () => {
  const location = useLocation();

  useEffect(() => {
    console.error(
      "404 Error: User attempted to access non-existent route:",
      location.pathname,
    );
  }, [location.pathname]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-background flex items-center justify-center px-4">
      {/* Background decoration */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-primary/5 rounded-full blur-3xl" />
      <div className="absolute bottom-0 left-0 w-96 h-96 bg-secondary/5 rounded-full blur-3xl" />

      <div className="text-center z-10">
        <div className="mb-6">
          <h1 className="text-7xl font-bold mb-4 bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            404
          </h1>
        </div>
        <h2 className="text-3xl font-bold mb-4">Page Not Found</h2>
        <p className="text-lg text-muted-foreground max-w-md mx-auto mb-8">
          Oops! The page you're looking for doesn't exist. It might have been moved or deleted.
        </p>
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link to="/">
            <Button className="bg-gradient-to-r from-primary to-secondary hover:opacity-90">
              Back to Home
            </Button>
          </Link>
          <Link to="/app">
            <Button variant="outline">
              Go to App
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
