/** Mirrors backend `shared.api.ApiError`, returned by every endpoint on failure. */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  details: string[];
}
