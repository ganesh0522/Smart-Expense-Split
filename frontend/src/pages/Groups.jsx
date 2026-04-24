import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

export default function Groups() {

  const [groups, setGroups] = useState([]);
  const [users, setUsers] = useState([]);

  const [name, setName] = useState("");
  const [selectedUsers, setSelectedUsers] = useState([]);

  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  // ================= GET CURRENT USER =================
  const getCurrentUserId = () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return null;

      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.userId;
    } catch {
      return null;
    }
  };

  const currentUserId = getCurrentUserId();

  // ================= FETCH DATA =================
  const fetchData = async () => {
    try {
      const [gRes, uRes] = await Promise.all([
        api.get("/groups"),
        api.get("/users")
      ]);

      // ✅ REMOVE DUPLICATE USERS
      const uniqueUsers = Array.from(
        new Map(uRes.data.map(u => [u.id, u])).values()
      );

      // ✅ REMOVE DUPLICATE GROUPS
      const uniqueGroups = Array.from(
        new Map(gRes.data.map(g => [g.id, g])).values()
      );

      setUsers(uniqueUsers);
      setGroups(uniqueGroups);

    } catch (err) {
      console.error(err);
      setError("Failed to load data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  // ================= TOGGLE USER =================
  const toggleUser = (id) => {
    setSelectedUsers(prev =>
      prev.includes(id)
        ? prev.filter(u => u !== id)
        : [...prev, id]
    );
  };

  // ================= CREATE GROUP =================
  const createGroup = async () => {

    if (!name.trim()) {
      alert("Enter group name");
      return;
    }

    if (creating) return;

    setCreating(true);

    try {
      await api.post("/groups", {
        name: name.trim(),
        memberIds: [...new Set(selectedUsers)] // ✅ FIX DUPLICATES
      });

      setName("");
      setSelectedUsers([]);
      fetchData();

    } catch (err) {
      console.error(err);
      alert("Failed to create group");
    } finally {
      setCreating(false);
    }
  };

  // ================= STATES =================
  if (loading) {
    return <p className="text-center mt-10">Loading groups...</p>;
  }

  if (error) {
    return <p className="text-center text-red-500 mt-10">{error}</p>;
  }

  // ================= UI =================
  return (
    <div className="space-y-6">

      <h1 className="text-2xl font-bold">Groups</h1>

      {/* CREATE */}
      <div className="bg-white p-5 rounded-xl shadow space-y-4">

        <input
          className="border p-2 rounded w-full"
          placeholder="Enter group name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <div>
          <h3 className="font-medium mb-2">Select Members</h3>

          <div className="grid grid-cols-2 gap-2 border p-2 rounded max-h-40 overflow-y-auto">

            {users
              .filter(u => u.id !== currentUserId)
              .map(user => (
                <label
                  key={user.id}
                  className="flex items-center gap-2 text-sm cursor-pointer"
                >
                  <input
                    type="checkbox"
                    checked={selectedUsers.includes(user.id)}
                    onChange={() => toggleUser(user.id)}
                  />
                  {user.name}
                </label>
              ))}

          </div>
        </div>

        <button
          disabled={creating}
          className="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50"
          onClick={createGroup}
        >
          {creating ? "Creating..." : "Create Group"}
        </button>

      </div>

      {/* EMPTY STATE */}
      {groups.length === 0 && (
        <p className="text-center text-gray-500">
          No groups yet. Create one 🚀
        </p>
      )}

      {/* LIST */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

        {groups.map(g => (
          <div
            key={g.id}
            onClick={() => navigate(`/groups/${g.id}`)}
            className="bg-white p-5 rounded-xl shadow hover:shadow-lg cursor-pointer transition"
          >
            <h2 className="font-semibold">{g.name}</h2>

            <p className="text-sm text-gray-500 mt-1">
              Members: {g.members.map(m => m.name).join(", ")}
            </p>
          </div>
        ))}

      </div>

    </div>
  );
}