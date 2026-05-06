const API_BASE_URL = 'http://localhost:8080';

export const authService = {
  async login(credentials: any) {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials),
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'Invalid username or password');
    }
    const data = await response.json();
    localStorage.setItem('token', data.jwt);
    localStorage.setItem('userId', data.userId.toString());
    return data;
  },

  async signup(userData: any) {
    const response = await fetch(`${API_BASE_URL}/api/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData),
    });
    if (!response.ok) throw new Error('Signup failed');
    return response.text();
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
  }
};

export const fileService = {
  async upload(file: File) {
    const token = localStorage.getItem('token');
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/api/files/upload`, {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${token}` 
      },
      body: formData,
    });
    if (!response.ok) throw new Error('Upload failed');
    return response.json();
  },

  async delete(id: string) {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API_BASE_URL}/api/files/${id}`, {
      method: 'DELETE',
      headers: { 
        'Authorization': `Bearer ${token}` 
      },
    });
    if (!response.ok) throw new Error('Delete failed');
    return true;
  }
};
