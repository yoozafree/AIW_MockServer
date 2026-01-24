import axios from 'axios';


export async function upload(file: File): Promise<FileData> {
  const formData = new FormData();
  formData.append('file', file);
  const response = await axios.post('/fileUpload', formData);
  return response.data;
}

export class FileData {
  uid?: string;
  fileName?: string;
}
