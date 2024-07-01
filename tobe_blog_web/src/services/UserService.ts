import { AxiosPromise } from 'axios';

import { server } from '.';

const USER_URI = 'v1/users';

export function getUsers(size: number, current: number): AxiosPromise {
  return server.get(`/${USER_URI}?size=${size}&current=${current + 1}`);
}

export function deleteUser(id: number | string): AxiosPromise {
  return server.delete(`/${USER_URI}/${id}`);
}

export function createUser(data: { firstName: string | undefined; lastName: string | undefined; email: string | undefined; password: string | undefined }): AxiosPromise {
  return server.post(`/${USER_URI}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

interface Features {
  articleModule: boolean;
  planModule: boolean;
  vocabularyModule: boolean;
}

export function updateUser(data: {
  id: string | undefined;
  email: string | undefined;
  username: string | undefined;
  firstName: string | undefined;
  lastName: string | undefined;
  phoneNum: string | undefined;
  address: string | undefined;
  avatarUrl: string | undefined;
  introduction: string | undefined;
  blog: string | undefined;
  profession: string | undefined;
  backgroundImg: string | undefined;
  photoImg: string | undefined;
  features: Features | undefined;
}): AxiosPromise {
  return server.put(`/${USER_URI}/${data.id}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}
