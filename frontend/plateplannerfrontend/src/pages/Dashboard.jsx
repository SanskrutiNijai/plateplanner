import { useEffect, useState } from "react";
import { Box, Button, TextField, MenuItem, Typography, Container, Paper } from "@mui/material";
import { useNavigate } from "react-router-dom";
import RecipeList from "../components/RecipeList";
import RecipeForm from "../components/RecipeForm";
import RecommendationDrawer from "../components/RecommendationDrawer";
import PaymentButton from "../components/PaymentButton";
import {
  getAllRecipes, addRecipe, updateRecipe, deleteRecipe,
  getRecommendation, ensureUser, getCurrentUser
} from "../api/recipeApi";

export default function Dashboard({ token, premium, setPremium }) {
  const [recipes, setRecipes] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [search, setSearch] = useState("");
  const [filterType, setFilterType] = useState("");
  const [editing, setEditing] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [recommendation, setRecommendation] = useState(null);

  const navigate = useNavigate();

  const loadRecipes = async () => {
    const res = await getAllRecipes(token);
    setRecipes(res.data);
    setFiltered(res.data);
  };

  useEffect(() => {
    loadRecipes();
  }, []);

  useEffect(() => {
    if (!token) return;
    (async () => {
      try { await ensureUser(token); } catch {}
      try {
        const me = await getCurrentUser(token);
        setPremium(Boolean(me.data?.premium));
      } catch {}
    })();
  }, [token, setPremium]);

  const handleSearch = (e) => {
    const term = e.target.value.toLowerCase();
    setSearch(term);
    setFiltered(recipes.filter((r) => r.name.toLowerCase().includes(term)));
  };

  const handleFilter = (type) => {
    setFilterType(type);
    setFiltered(type ? recipes.filter((r) => r.type === type) : recipes);
  };

  const handleAdd = async (data) => {
    await addRecipe(data, token);
    setShowForm(false);
    loadRecipes();
  };

  const handleUpdate = async (data) => {
    await updateRecipe(editing.id, data, token);
    setEditing(null);
    setShowForm(false);
    loadRecipes();
  };

  const handleDelete = async (id) => {
    await deleteRecipe(id, token);
    loadRecipes();
  };

  const handleRecommend = async (id) => {
    const res = await getRecommendation(id, token);
    setRecommendation(res.data);
    setDrawerOpen(true);
  };

  return (
    <Container maxWidth={false} sx={{ mt: 4, mb: 8 }}>
      <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: "primary.dark" }}>
          Your PlatePlanner Dashboard
        </Typography>
        <Box sx={{ ml: "auto", display: "flex", gap: 1 }}>
          {premium && (
            <Button variant="outlined" color="secondary" onClick={() => navigate("/premium")}>
              Premium Access
            </Button>
          )}
        </Box>
      </Box>

      <Paper elevation={1} sx={{ p: 2, mb: 3, display: "flex", gap: 2, alignItems: "center", flexWrap: "wrap" }}>
        <TextField label="Search Recipes" value={search} onChange={handleSearch} size="small" sx={{ minWidth: 200 }} />
        <TextField
          select
          label="Filter by Type"
          value={filterType}
          onChange={(e) => handleFilter(e.target.value)}
          size="small"
          sx={{ width: 150 }}
        >
          <MenuItem value="">All Recipes</MenuItem>
          <MenuItem value="breakfast">Breakfast 🥞</MenuItem>
          <MenuItem value="lunch">Lunch 🥗</MenuItem>
          <MenuItem value="dinner">Dinner 🥩</MenuItem>
        </TextField>
        <Box sx={{ ml: "auto" }}>
          <Button variant="contained" color="secondary" onClick={() => setShowForm(true)} size="medium">
            + Add New Recipe
          </Button>
        </Box>
      </Paper>

      {showForm && (
        <Paper elevation={3} sx={{ p: 4, mb: 3 }}>
          <RecipeForm
            onSubmit={editing ? handleUpdate : handleAdd}
            initialData={editing}
            onCancel={() => {
              setEditing(null);
              setShowForm(false);
            }}
          />
        </Paper>
      )}

      <RecipeList
        recipes={filtered}
        onEdit={(r) => { setEditing(r); setShowForm(true); }}
        onDelete={handleDelete}
        onRecommend={handleRecommend}
      />

      <RecommendationDrawer open={drawerOpen} onClose={() => setDrawerOpen(false)} recommendation={recommendation} />

      {/* Hide upgrade FAB once user is premium */}
      {!premium && <PaymentButton token={token} onActivated={() => setPremium(true)} />}
    </Container>
  );
}
