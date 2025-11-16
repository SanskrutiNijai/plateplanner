import { useContext, useEffect, useMemo, useRef, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardMedia,
  Container,
  Dialog,
  DialogContent,
  DialogTitle,
  Divider,
  Grid,
  IconButton,
  LinearProgress,
  Paper,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import UploadIcon from "@mui/icons-material/Upload";
import DeleteIcon from "@mui/icons-material/Delete";
import DownloadIcon from "@mui/icons-material/Download";
import VisibilityIcon from "@mui/icons-material/Visibility";
import AutoAwesomeIcon from "@mui/icons-material/AutoAwesome";
import RefreshIcon from "@mui/icons-material/Refresh";

import RecommendationDrawer from "../components/RecommendationDrawer";
import {
  listMyImages,
  uploadImage,
  deleteImageById,
  recommendForImage,
  fetchImageBlob,
  fetchImageDownloadBlob,
} from "../api/recipeApi";



export default function PremiumDashboard({ premium }) {
  const navigate = useNavigate();
  const { token } = useContext(AuthContext);

  // redirect non-premium back to home (match your existing behavior)
  useEffect(() => {
    if (premium === false) navigate("/", { replace: true });
  }, [premium, navigate]);

  // ------------------ state ------------------
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);

  // id -> object URL for preview
  const [imgUrls, setImgUrls] = useState({});
  const urlCacheRef = useRef({}); // to revoke later

  const [preview, setPreview] = useState(null); // { id, filename }
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [recommendation, setRecommendation] = useState(null);

  const fileInputRef = useRef(null);

  const hasToken = useMemo(() => !!token, [token]);

  // ------------------ helpers ------------------
  const revokeUrl = (id) => {
    const url = urlCacheRef.current[id];
    if (url) {
      URL.revokeObjectURL(url);
      delete urlCacheRef.current[id];
    }
  };

  const setUrlForId = (id, url) => {
    // Revoke old, set new
    revokeUrl(id);
    urlCacheRef.current[id] = url;
    setImgUrls((prev) => ({ ...prev, [id]: url }));
  };

  // Fetch and cache object URL for an image id
  const ensureObjectUrl = async (id) => {
    if (imgUrls[id]) return imgUrls[id];
    const res = await fetchImageBlob(id, token);
    const url = URL.createObjectURL(res.data);
    setUrlForId(id, url);
    return url;
  };

  const loadImages = async () => {
    if (!hasToken) return;
    setLoading(true);
    try {
      const res = await listMyImages(token);
      const list = res.data || [];
      setImages(list);

      // Preload object URLs (optional: do it lazily; here we do eager but safe)
      await Promise.all(
        list.map(async (x) => {
          try {
            if (!imgUrls[x.id]) {
              const r = await fetchImageBlob(x.id, token);
              const url = URL.createObjectURL(r.data);
              setUrlForId(x.id, url);
            }
          } catch (e) {
            // If blob fetch fails, ignore; tile will simply not render image
            console.error("Preview load failed", e);
          }
        })
      );
    } catch (e) {
      console.error(e);
      alert("Failed to load images.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadImages();
    return () => {
      // cleanup all object URLs
      Object.keys(urlCacheRef.current).forEach((id) => revokeUrl(id));
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [hasToken]);

  const handlePickFile = () => fileInputRef.current?.click();

  const handleUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file || !hasToken) return;
    setUploading(true);
    try {
      await uploadImage(file, token);
      await loadImages();
    } catch (err) {
      console.error(err);
      alert("Upload failed.");
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = "";
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this image?")) return;
    try {
      await deleteImageById(id, token);
      // remove from UI and revoke URL
      setImages((prev) => prev.filter((i) => i.id !== id));
      setImgUrls((prev) => {
        const copy = { ...prev };
        delete copy[id];
        return copy;
      });
      revokeUrl(id);
    } catch (e) {
      console.error(e);
      alert("Delete failed.");
    }
  };

  const handleRecommend = async (imageId) => {
    try {
      const res = await recommendForImage(imageId, token);
      setRecommendation(res.data);
      setDrawerOpen(true);
    } catch (e) {
      console.error(e);
      alert("Recommendation failed.");
    }
  };

  const handlePreview = async (img) => {
    try {
      await ensureObjectUrl(img.id);
      setPreview(img);
    } catch (e) {
      console.error(e);
      alert("Could not preview image.");
    }
  };

  const handleDownload = async (id, filename = "image") => {
    try {
      const res = await fetchImageDownloadBlob(id, token);
      const url = URL.createObjectURL(res.data);
      const a = document.createElement("a");
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (e) {
      console.error(e);
      alert("Download failed.");
    }
  };

  // ------------------ render ------------------
  return (
    <Container maxWidth={false} sx={{ mt: 4, mb: 8 }}>
      <Box sx={{ display: "flex", alignItems: "center", mb: 2, gap: 2 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: "primary.dark" }}>
          Premium Image Intelligence
        </Typography>
        <Box sx={{ ml: "auto", display: "flex", gap: 1 }}>
          <Button
            variant="outlined"
            onClick={() => navigate("/")}
          >
            ← Back to Home
          </Button>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadImages}
            disabled={loading}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            color="secondary"
            startIcon={<UploadIcon />}
            onClick={handlePickFile}
            disabled={uploading}
          >
            Upload Image
          </Button>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleUpload}
            style={{ display: "none" }}
          />
        </Box>
      </Box>

      <Paper elevation={1} sx={{ p: 2, mb: 3 }}>
        <Typography variant="body1" color="text.secondary">
          Upload a photo of a recipe or dish. We’ll store it securely and you can ask AI to extract
          recipe details and suggestions. Only you can view, download, or delete your images.
        </Typography>
      </Paper>

      {uploading && (
        <Box sx={{ mb: 2 }}>
          <LinearProgress />
        </Box>
      )}

      {loading ? (
        <Box sx={{ mt: 4 }}>
          <LinearProgress />
        </Box>
      ) : images.length === 0 ? (
        <Paper
          elevation={0}
          sx={{ p: 6, textAlign: "center", border: "1px dashed", borderColor: "divider" }}
        >
          <Typography variant="h6" gutterBottom>
            No images yet
          </Typography>
          <Typography color="text.secondary" sx={{ mb: 2 }}>
            Click “Upload Image” to get started.
          </Typography>
          <Button variant="contained" startIcon={<UploadIcon />} onClick={handlePickFile}>
            Upload Image
          </Button>
        </Paper>
      ) : (
        <Grid container spacing={3}>
          {images.map((img) => {
            const url = imgUrls[img.id];
            return (
              <Grid item xs={12} sm={6} md={4} key={img.id}>
                <Card sx={{ height: "100%", display: "flex", flexDirection: "column" }}>
                  {url ? (
                    <CardMedia
                      component="img"
                      height="220"
                      image={url}
                      alt={img.filename}
                      sx={{ objectFit: "cover", cursor: "pointer" }}
                      onClick={() => handlePreview(img)}
                    />
                  ) : (
                    <Box
                      sx={{
                        height: 220,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        bgcolor: "action.hover",
                      }}
                    >
                      <Typography variant="body2" color="text.secondary">
                        Loading preview…
                      </Typography>
                    </Box>
                  )}
                  <CardContent sx={{ flexGrow: 1 }}>
                    <Stack spacing={0.5}>
                      <Typography variant="subtitle1" noWrap title={img.filename}>
                        {img.filename}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {img.contentType} • {(img.size / 1024).toFixed(1)} KB
                      </Typography>
                    </Stack>
                    <Divider sx={{ my: 1.5 }} />
                    <Stack direction="row" spacing={1} flexWrap="wrap">
                      <Tooltip title="Preview">
                        <IconButton onClick={() => handlePreview(img)}>
                          <VisibilityIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Download">
                        <IconButton onClick={() => handleDownload(img.id, img.filename || "image")}>
                          <DownloadIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="AI Recommend">
                        <IconButton color="primary" onClick={() => handleRecommend(img.id)}>
                          <AutoAwesomeIcon />
                        </IconButton>
                      </Tooltip>
                      <Box sx={{ flexGrow: 1 }} />
                      <Tooltip title="Delete">
                        <IconButton color="error" onClick={() => handleDelete(img.id)}>
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </Stack>
                  </CardContent>
                  <CardActions />
                </Card>
              </Grid>
            );
          })}
        </Grid>
      )}

      {/* Image preview dialog */}
      <Dialog open={!!preview} onClose={() => setPreview(null)} maxWidth="md" fullWidth>
        <DialogTitle>{preview?.filename || "Preview"}</DialogTitle>
        <DialogContent>
          {preview && imgUrls[preview.id] && (
            <Box sx={{ width: "100%", textAlign: "center" }}>
              <img
                src={imgUrls[preview.id]}
                alt={preview.filename}
                style={{ maxWidth: "100%", borderRadius: 8 }}
              />
            </Box>
          )}
        </DialogContent>
      </Dialog>

      {/* AI result drawer (reuse existing component) */}
      <RecommendationDrawer
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        recommendation={recommendation}
      />
    </Container>
  );
}
