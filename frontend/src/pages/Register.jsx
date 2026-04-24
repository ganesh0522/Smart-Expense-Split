import { useState } from "react";
import api from "../services/api";
import { useNavigate, Link } from "react-router-dom";

export default function Register() {

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const navigate = useNavigate();

  const handleRegister = async () => {

    // 🔹 Validation
    if (!name || !email || !password) {
      setError("All fields are required");
      return;
    }

    if (password.length < 6) {
      setError("Password must be at least 6 characters");
      return;
    }

    try {
      setLoading(true);
      setError("");
      setSuccess("");

      await api.post("/auth/register", {
        name,
        email,
        password
      });

      setSuccess("Account created successfully!");

      // Redirect after short delay
      setTimeout(() => {
        navigate("/");
      }, 1000);

    } catch (err) {
      console.error(err);

      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Registration failed");
      }

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center h-[70vh]">

      <div className="bg-white p-8 rounded-2xl shadow-lg w-96">

        <h2 className="text-2xl font-bold mb-6 text-center">
          Create Account 🚀
        </h2>

        {/* Error */}
        {error && (
          <p className="text-red-500 text-sm mb-3">{error}</p>
        )}

        {/* Success */}
        {success && (
          <p className="text-green-500 text-sm mb-3">{success}</p>
        )}

        {/* Name */}
        <input
          className="border p-2 w-full mb-4 rounded focus:outline-none focus:ring-2 focus:ring-green-400"
          placeholder="Full Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        {/* Email */}
        <input
          className="border p-2 w-full mb-4 rounded focus:outline-none focus:ring-2 focus:ring-green-400"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        {/* Password */}
        <input
          className="border p-2 w-full mb-4 rounded focus:outline-none focus:ring-2 focus:ring-green-400"
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {/* Button */}
        <button
          className="bg-green-600 text-white w-full p-2 rounded hover:bg-green-700 transition"
          onClick={handleRegister}
          disabled={loading}
        >
          {loading ? "Creating..." : "Register"}
        </button>

        {/* Footer */}
        <p className="text-center mt-4 text-sm">
          Already have an account?{" "}
          <Link to="/" className="text-blue-500 hover:underline">
            Login
          </Link>
        </p>

      </div>

    </div>
  );
}