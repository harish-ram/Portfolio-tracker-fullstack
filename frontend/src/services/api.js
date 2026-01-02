import axios from 'axios';
const API_BASE_URL = (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE_URL) || process.env.API_BASE_URL || 'http://localhost:8080/api';
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000,
    withCredentials: true,
});
api.interceptors.response.use((response) => response, (error) => {
    if (error.code === 'ECONNABORTED') {
        error.message = 'Request timeout. Please check your connection.';
    }
    return Promise.reject(error);
});
export const stockApi = {
    getAll: (level) => {
        const params = level ? { level } : {};
        return api.get('/stocks', { params });
    },
    getBySymbol: (symbol) => api.get(`/stocks/${symbol}`),
    create: (stock) => api.post('/stocks', stock),
    update: (symbol, stock) => api.put(`/stocks/${symbol}`, stock),
    delete: (symbol) => api.delete(`/stocks/${symbol}`),
    setLevel: (symbol, level) => api.put(`/stocks/${symbol}/level`, null, { params: { level } }),
    search: (symbol) => api.get('/stocks/search', { params: { symbol } }),
};
export const portfolioApi = {
    get: () => api.get('/portfolio'),
    getPositions: (page = 0, size = 50) => api.get('/portfolio/positions', { params: { page, size } }),
    getPosition: (symbol) => api.get(`/portfolio/positions/${symbol}`),
};
export const transactionApi = {
    getAll: (page = 0, size = 50) => api.get('/transactions', { params: { page, size } }),
    getById: (id) => api.get(`/transactions/${id}`),
    create: (transaction) => api.post('/transactions', transaction),
    delete: (id) => api.delete(`/transactions/${id}`),
    getPrice: (symbol) => api.get('/transactions/price', { params: { symbol } }),
};
export default api;
