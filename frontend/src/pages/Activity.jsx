import { useEffect, useState } from "react";
import api from "../services/api";

export default function Activity() {

  const [activities, setActivities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // ================= FETCH =================
  const fetchActivities = async () => {
    try {
      const res = await api.get("/timeline"); // ✅ CORRECT ENDPOINT
      setActivities(res.data || []);
    } catch (err) {
      console.error(err);
      setError("Failed to load activity");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchActivities();
  }, []);

  // ================= HELPERS =================

  const formatTime = (ts) => {
    if (!ts) return "";
    return new Date(ts).toLocaleString("en-IN", {
      day: "2-digit",
      month: "short",
      hour: "2-digit",
      minute: "2-digit"
    });
  };

  const getIcon = (action) => {
    switch (action) {
      case "ADD_EXPENSE":
        return "💸";
      case "CREATE_GROUP":
        return "👥";
      case "SETTLE_PAYMENT":
        return "✅";
      default:
        return "📌";
    }
  };

  // ================= STATES =================

  if (loading) {
    return <p className="text-center mt-10">Loading activity...</p>;
  }

  if (error) {
    return (
      <p className="text-center mt-10 text-red-500">
        {error}
      </p>
    );
  }

  // ================= UI =================

  return (
    <div className="space-y-6">

      <h1 className="text-2xl font-bold">Activity</h1>

      <div className="bg-white rounded-2xl shadow p-5">

        {activities.length === 0 ? (
          <p className="text-gray-500 text-center">
            No activity yet 🚀
          </p>
        ) : (
          <div className="space-y-4">

            {activities.map((a, i) => (

              <div
                key={i}
                className="flex items-start gap-3 border-b pb-3 last:border-none"
              >

                {/* ICON */}
                <div className="text-xl mt-1">
                  {getIcon(a.action)}
                </div>

                {/* CONTENT */}
                <div className="flex-1">

                  <p className="text-sm font-medium">
                    {a.message}
                  </p>

                  <p className="text-xs text-gray-400 mt-1">
                    {formatTime(a.timestamp)}
                  </p>

                </div>

              </div>

            ))}

          </div>
        )}

      </div>

    </div>
  );
}