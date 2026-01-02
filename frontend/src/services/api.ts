import axios from 'axios';
import { Stock, Position, Transaction, Portfolio } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
  withCredentials: true,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.code === 'ECONNABORTED') {
      error.message = 'Request timeout. Please check your connection.';
    }
    return Promise.reject(error);
  }
);

export interface StockPrice {
  symbol: string;
  name: string;
  price: number;
  bid: number;
  ask: number;
  open: number;
  dayHigh: number;
  dayLow: number;
  yearHigh: number;
  yearLow: number;
  previousClose: number;
  volume: number;
}

export const stockApi = {
  getAll: (level?: string) => {
    const params = level ? { level } : {};
    return api.get<Stock[]>('/stocks', { params });
  },
  getBySymbol: (symbol: string) => api.get<Stock>(`/stocks/${symbol}`),
  create: (stock: Partial<Stock>) => api.post<Stock>('/stocks', stock),
  update: (symbol: string, stock: Partial<Stock>) =>
    api.put<Stock>(`/stocks/${symbol}`, stock),
  delete: (symbol: string) => api.delete(`/stocks/${symbol}`),
  setLevel: (symbol: string, level: string) =>
    api.put(`/stocks/${symbol}/level`, null, { params: { level } }),
  search: (symbol: string) => api.get<StockPrice>('/stocks/search', { params: { symbol } }),
};

export const portfolioApi = {
  get: () => api.get<Portfolio>('/portfolio'),
  getPositions: (page = 0, size = 50) => 
    api.get<Position[]>('/portfolio/positions', { params: { page, size } }),
  getPosition: (symbol: string) =>
    api.get<Position>(`/portfolio/positions/${symbol}`),
};

export const transactionApi = {
  getAll: (page = 0, size = 50) => 
    api.get<Transaction[]>('/transactions', { params: { page, size } }),
  getById: (id: number) => api.get<Transaction>(`/transactions/${id}`),
  create: (transaction: Partial<Transaction>) =>
    api.post<Transaction>('/transactions', transaction),
  delete: (id: number) => api.delete(`/transactions/${id}`),
  getPrice: (symbol: string) => api.get<StockPrice>('/transactions/price', { params: { symbol } }),
};

export default api;
