import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useContext } from "react";

import { AuthContext } from "./context/AuthContext";

// Pages
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Groups from "./pages/Groups";
import GroupDetails from "./pages/GroupDetails";
import Activity from "./pages/Activity";

// Components
import Navbar from "./components/Navbar";

function App() {

  const { token } = useContext(AuthContext);

  // 🔐 Protected Route
  const PrivateRoute = ({ children }) => {
    return token ? children : <Navigate to="/" />;
  };

  // 🔁 Prevent logged-in users from visiting login/register
  const PublicRoute = ({ children }) => {
    return !token ? children : <Navigate to="/dashboard" />;
  };

  return (
    <BrowserRouter>

      <div className="min-h-screen bg-gray-100">

        {/* Navbar only when logged in */}
        {token && <Navbar />}

        <div className="max-w-6xl mx-auto p-6">

          <Routes>

            {/* PUBLIC ROUTES */}
            <Route
              path="/"
              element={
                <PublicRoute>
                  <Login />
                </PublicRoute>
              }
            />

            <Route
              path="/register"
              element={
                <PublicRoute>
                  <Register />
                </PublicRoute>
              }
            />

            {/* PRIVATE ROUTES */}
            <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <Dashboard />
                </PrivateRoute>
              }
            />

            <Route
              path="/groups"
              element={
                <PrivateRoute>
                  <Groups />
                </PrivateRoute>
              }
            />

            <Route
              path="/groups/:id"
              element={
                <PrivateRoute>
                  <GroupDetails />
                </PrivateRoute>
              }
            />

            <Route path="/activity" element={<Activity />} />

            {/* FALLBACK */}
            <Route path="*" element={<Navigate to="/" />} />

          </Routes>

        </div>

      </div>

    </BrowserRouter>
  );
}

export default App;