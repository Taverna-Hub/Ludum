import api from '../axios';

export async function getRequest(endpoint: string, params?: Record<string, any>) {
  try {
    const response = await api.get(endpoint, { params });
    return response.data;
  } catch (error) {
    console.error('GET request error:', error);
    throw error;
  }
};

export async function postRequest(endpoint: string, data: Record<string, any>) {
  try {
    const response = await api.post(endpoint, data);
    return response.data;
  } catch (error) {
    console.error('POST request error:', error);
    throw error;
  }
};

export async function putRequest(endpoint: string, data: Record<string, any>) {
  try {
    const response = await api.put(endpoint, data);
    return response.data;
  } catch (error) {
    console.error('PUT request error:', error);
    throw error;
  }
};

export async function deleteRequest(endpoint: string) {
  try {
    const response = await api.delete(endpoint);
    return response.data;
  } catch (error) {
    console.error('DELETE request error:', error);
    throw error;
  }
};


export async function patchRequest(endpoint: string, data: Record<string, any>) {
  try {
    const response = await api.patch(endpoint, data);
    return response.data;
  } catch (error) {
    console.error('PATCH request error:', error);
    throw error;
  }
};

export async function headRequest(endpoint: string, params?: Record<string, any>) {
  try {
    const response = await api.head(endpoint, { params });
    return response.data;
  } catch (error) {
    console.error('HEAD request error:', error);
    throw error;
  }
}