import { axiosInstance } from '../api/api';

type AdminLoginRequest = {
  username: string;
  password: string;
};

type JwtResponse = {
  token: string;
};

export const adminLogin = async (credentials: AdminLoginRequest): Promise<JwtResponse> => {
  const response = await axiosInstance.post('/admin-login', credentials);
  return response.data;
};
