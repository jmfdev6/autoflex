import { ApiResponse, ApiError } from '@/types/api';
import { fetchWithRetry, parseErrorResponse } from '@/utils/errorHandler';

// Backend serves under /api/v1; normalize so .../api becomes .../api/v1
const raw = import.meta.env.VITE_API_URL || 'http://localhost:8081/api/v1';
const normalized = raw.replace(/\/+$/, '');
const API_BASE_URL =
  normalized.endsWith('/api') && !normalized.endsWith('/api/v1')
    ? `${normalized}/v1`
    : normalized;

export class ResponseError extends Error {
  constructor(
    message: string,
    public status: number,
    public apiError: ApiError
  ) {
    super(message);
    this.name = 'ResponseError';
  }
}

export class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string = API_BASE_URL) {
    const normalized = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
    this.baseUrl = (normalized.endsWith('/api') && !normalized.endsWith('/api/v1'))
      ? `${normalized}/v1`
      : normalized;
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    const url = `${this.baseUrl}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;

    const defaultHeaders: HeadersInit = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    const config: RequestInit = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    };

    try {
      const response = await fetchWithRetry(url, config);

      if (!response.ok) {
        const error = await parseErrorResponse(response);
        throw new ResponseError(error.message, response.status, error);
      }

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const data = await response.json();
        return data as ApiResponse<T>;
      }

      // Handle non-JSON responses
      const text = await response.text();
      return {
        success: true,
        data: text as unknown as T,
      };
    } catch (error) {
      if (error instanceof ResponseError) {
        throw error;
      }

      // Network or other errors
      const apiError: ApiError = {
        message: error instanceof Error ? error.message : 'Network error occurred',
        code: 'NETWORK_ERROR',
      };
      throw new ResponseError(apiError.message, 0, apiError);
    }
  }

  async get<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'GET',
    });
  }

  async post<T>(endpoint: string, data: unknown): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async put<T>(endpoint: string, data: unknown): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async delete<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: 'DELETE',
    });
  }
}

// Export singleton instance
export const apiClient = new ApiClient();
