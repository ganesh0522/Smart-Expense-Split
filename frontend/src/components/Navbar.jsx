import { Link, useNavigate, useLocation } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";

export default function Navbar() {

  const { token, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <div className="bg-white shadow-md">

      <div className="max-w-6xl mx-auto flex justify-between items-center px-6 py-4">

        {/* Logo */}
        <h1 className="text-xl font-bold text-blue-600">
          Expense Splitter
        </h1>

        {/* Right Side */}
        <div className="flex items-center gap-6">

          {token && (
            <>
              <Link
                to="/dashboard"
                className={`font-medium ${
                  isActive("/dashboard") ? "text-blue-600" : "text-gray-600"
                } hover:text-blue-600`}
              >
                Dashboard
              </Link>

              <Link
                to="/groups"
                className={`font-medium ${
                  isActive("/groups") ? "text-blue-600" : "text-gray-600"
                } hover:text-blue-600`}
              >
                Groups
              </Link>

              <Link to="/activity" className={`font-medium ${isActive("/activity") ? "text-blue-600" : "text-gray-600"} hover:text-blue-600`}>
                Activity
              </Link>
            </>
          )}

          {/* Auth Buttons */}
          {!token ? (
            <>
              <Link
                to="/"
                className="text-gray-600 hover:text-blue-600"
              >
                Login
              </Link>

              <Link
                to="/register"
                className="bg-blue-600 text-white px-4 py-1 rounded hover:bg-blue-700"
              >
                Register
              </Link>
            </>
          ) : (
            <button
              onClick={handleLogout}
              className="bg-red-500 text-white px-4 py-1 rounded hover:bg-red-600"
            >
              Logout
            </button>
          )}

        </div>

      </div>

    </div>
  );
}