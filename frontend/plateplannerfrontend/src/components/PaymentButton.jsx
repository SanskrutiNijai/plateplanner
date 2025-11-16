import { Fab } from "@mui/material";
import PaymentIcon from "@mui/icons-material/Payment";
import { createOrder, getCurrentUser } from "../api/recipeApi";

export default function PaymentButton({ token, onActivated }) {
  const handlePayment = async () => {
    try {
      // 1) Create Razorpay order (server binds user via JWT → notes.user_keycloak_id)
      const res = await createOrder(1000, "INR", token);
      const order = res.data;

      // 2) Open Razorpay checkout
      const options = {
        key: import.meta.env.VITE_PAYMENT_KEY_ID, // TODO: replace with your key-id
        amount: order.amount,
        currency: order.currency,
        name: "PlatePlanner",
        description: "Premium Upgrade",
        order_id: order.id,

        handler: async function () {
          //alert("✅ Payment successful! Activating Premium...");

          // 3) Poll until webhook flips premium=true, then update state + redirect
          const maxTries = 20, delayMs = 1500;
          for (let i = 0; i < maxTries; i++) {
            try {
              const me = await getCurrentUser(token);
              if (me.data?.premium) {
                onActivated?.();          // hide FAB everywhere
                window.location.href = "/premium"; // go to premium dashboard
                return;
              }
            } catch {}
            await new Promise((r) => setTimeout(r, delayMs));
          }
         // alert("Payment succeeded but activation is still processing. Please refresh in a moment.");
        },

        theme: { color: "#3399cc" },
      };

      new window.Razorpay(options).open();
    } catch (err) {
      console.error("Payment failed:", err);
      alert("Payment failed. Check console for details.");
    }
  };

  return (
    <Fab
      color="primary"
      variant="extended"
      sx={{ position: "fixed", bottom: 16, right: 16, zIndex: 1000 }}
      onClick={handlePayment}
    >
      <PaymentIcon sx={{ mr: 1 }} /> Upgrade to Premium
    </Fab>
  );
}
