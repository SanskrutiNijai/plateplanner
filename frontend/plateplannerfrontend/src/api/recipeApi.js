import axios from "axios";

const RECIPE_API = import.meta.env.VITE_RECIPE_API;
const AI_API = import.meta.env.VITE_AI_API;
const PAYMENT_API = import.meta.env.VITE_PAYMENT_API;
const USER_API = import.meta.env.VITE_USER_API;
const RI_BASE = import.meta.env.VITE_RI_BASE;

export const ensureUser = (token) =>
  axios.get(`${USER_API}/home`, {
    headers: { Authorization: `Bearer ${token}` },
  });

export const getCurrentUser = (token) =>
  axios.get(`${USER_API}/users/me`, {
    headers: { Authorization: `Bearer ${token}` },
  });  

export const getAllRecipes = async (token) =>
  axios.get(`${RECIPE_API}/all`, {
    headers: { Authorization: `Bearer ${token}` },
  });

export const addRecipe = async (data, token) =>
  axios.post(`${RECIPE_API}/add`, data, {
    headers: { Authorization: `Bearer ${token}` },
  });

export const updateRecipe = async (id, data, token) =>
  axios.put(`${RECIPE_API}/${id}`, data, {
    headers: { Authorization: `Bearer ${token}` },
  });

export const deleteRecipe = async (id, token) =>
  axios.delete(`${RECIPE_API}/${id}`, {
    headers: { Authorization: `Bearer ${token}` },
  });

// export const getRecommendation = async (id, token) =>
//   axios.post(`${AI_API}/generate/${id}`, {
//     headers: { Authorization: `Bearer ${token}` },
//   });


  export const getRecommendation = async (id, token) =>
  axios.post(`${AI_API}/generate/${id}`,
    {}, // empty body
    {
      headers: { Authorization: `Bearer ${token}` },
    });



export const createOrder = async (amount, currency, token) => {
  return axios.post(
    `${PAYMENT_API}/create-order?amount=${amount}&currency=${currency}`,
    {}, // empty body
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  
};

// ---------------- Image Intelligence APIs ----------------
// All routes are secured; we attach Authorization: Bearer <token>

export const listMyImages = (token) =>
  axios.get(`${RI_BASE}/images`, {
    headers: { Authorization: `Bearer ${token}` },
    //withCredentials: true,
  });

export const uploadImage = (file, token) => {
  const form = new FormData();
  form.append("file", file);
  return axios.post(`${RI_BASE}/images`, form, {
    headers: {
      Authorization: `Bearer ${token}`,
      // Let the browser set the boundary; don't manually set Content-Type
    },
    //withCredentials: true,
  });
};

export const deleteImageById = (id, token) =>
  axios.delete(`${RI_BASE}/images/${id}`, {
    headers: { Authorization: `Bearer ${token}` },
    //withCredentials: true,
  });

export const recommendForImage = (imageId, token) =>
  axios.post(`${RI_BASE}/recommend/${imageId}`, {}, {
    headers: { Authorization: `Bearer ${token}` },
    //withCredentials: true,
  });

/**
 * Because the image endpoints require Authorization,
 * <img src="..."> cannot send headers. We fetch blobs and create object URLs.
 */
export const fetchImageBlob = (id, token) =>
  axios.get(`${RI_BASE}/images/${id}/view`, {
    headers: { Authorization: `Bearer ${token}` },
    responseType: "blob",
    //withCredentials: true,
  });

export const fetchImageDownloadBlob = (id, token) =>
  axios.get(`${RI_BASE}/images/${id}/download`, {
    headers: { Authorization: `Bearer ${token}` },
    responseType: "blob",
    //withCredentials: true,
  });