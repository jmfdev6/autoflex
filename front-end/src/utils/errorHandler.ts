import { ApiError } from '@/types/api';

export interface RetryOptions {
  maxRetries?: number;
  retryDelay?: number;
  retryableStatuses?: number[];
}

const DEFAULT_RETRY_OPTIONS: Required<RetryOptions> = {
  maxRetries: 3,
  retryDelay: 1000,
  retryableStatuses: [408, 429, 500, 502, 503, 504],
};

/**
 * Calculates exponential backoff delay
 */
function getRetryDelay(attempt: number, baseDelay: number): number {
  return baseDelay * Math.pow(2, attempt);
}

/**
 * Checks if a status code is retryable
 */
function isRetryableStatus(status: number, retryableStatuses: number[]): boolean {
  return retryableStatuses.includes(status);
}

/**
 * Parses error response from API
 */
export async function parseErrorResponse(response: Response): Promise<ApiError> {
  let message = 'An error occurred';
  let details: unknown = null;

  try {
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      const data = await response.json();
      if (data.message) {
        message = data.message;
      } else if (typeof data === 'string') {
        message = data;
      }
      details = data;
    } else {
      message = await response.text() || message;
    }
  } catch {
    // If parsing fails, use default message
    message = `HTTP ${response.status}: ${response.statusText}`;
  }

  return {
    message,
    code: response.status.toString(),
    details,
  };
}

/**
 * Maps HTTP status codes to user-friendly messages
 */
export function getStatusMessage(status: number): string {
  const statusMessages: Record<number, string> = {
    400: 'Invalid request. Please check your input.',
    401: 'Authentication required. Please log in.',
    403: 'You do not have permission to perform this action.',
    404: 'The requested resource was not found.',
    408: 'Request timeout. Please try again.',
    409: 'A conflict occurred. The resource may already exist.',
    422: 'Validation error. Please check your input.',
    429: 'Too many requests. Please try again later.',
    500: 'Internal server error. Please try again later.',
    502: 'Bad gateway. The server is temporarily unavailable.',
    503: 'Service unavailable. Please try again later.',
    504: 'Gateway timeout. Please try again.',
  };

  return statusMessages[status] || `An error occurred (${status})`;
}

/**
 * Retries a fetch request with exponential backoff
 */
export async function fetchWithRetry(
  url: string,
  options: RequestInit = {},
  retryOptions: RetryOptions = {}
): Promise<Response> {
  const {
    maxRetries,
    retryDelay,
    retryableStatuses,
  } = { ...DEFAULT_RETRY_OPTIONS, ...retryOptions };

  let lastError: Error | null = null;

  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      const response = await fetch(url, options);

      // If successful or non-retryable error, return immediately
      if (response.ok || !isRetryableStatus(response.status, retryableStatuses)) {
        return response;
      }

      // If it's the last attempt, return the error response
      if (attempt === maxRetries) {
        return response;
      }

      // Wait before retrying (exponential backoff)
      const delay = getRetryDelay(attempt, retryDelay);
      await new Promise(resolve => setTimeout(resolve, delay));

      lastError = new Error(`Request failed with status ${response.status}`);
    } catch (error) {
      lastError = error instanceof Error ? error : new Error('Network error');

      // If it's the last attempt, throw the error
      if (attempt === maxRetries) {
        throw lastError;
      }

      // Wait before retrying (exponential backoff)
      const delay = getRetryDelay(attempt, retryDelay);
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  throw lastError || new Error('Request failed after retries');
}
