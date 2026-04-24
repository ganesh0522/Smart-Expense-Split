import { useEffect, useState } from "react";
import api from "../services/api";
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from "recharts";

export default function Dashboard() {

  const [summary, setSummary] = useState({
    youOwe: 0,
    youGet: 0,
    netBalance: 0
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // ================= FETCH =================
  const fetchSummary = async () => {
    try {
      const res = await api.get("/dashboard");

      setSummary({
        youOwe: Number(res.data.youOwe) || 0,
        youGet: Number(res.data.youGet) || 0,
        netBalance: Number(res.data.netBalance) || 0
      });

    } catch (err) {
      console.error("Dashboard error:", err);
      setError("Failed to load dashboard");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSummary();
  }, []);

  // ================= HELPERS =================
  const formatCurrency = (num) =>
    `₹${num.toLocaleString("en-IN")}`;

  const hasData = summary.youOwe > 0 || summary.youGet > 0;

  const chartData = hasData
    ? [
        { name: "You Owe", value: summary.youOwe },
        { name: "You Get", value: summary.youGet }
      ]
    : [{ name: "No Data", value: 1 }];

  const COLORS = hasData
    ? ["#ef4444", "#22c55e"]
    : ["#9ca3af"];

  // ================= STATES =================
  if (loading) {
    return <p className="text-center mt-10">Loading dashboard...</p>;
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

      {/* TITLE */}
      <h1 className="text-2xl font-bold">Dashboard</h1>

      {/* CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

        {/* YOU OWE */}
        <div className="bg-red-500 text-white p-6 rounded-2xl shadow-md">
          <p className="text-sm opacity-80">You Owe</p>
          <h2 className="text-2xl font-bold mt-2">
            {formatCurrency(summary.youOwe)}
          </h2>
        </div>

        {/* YOU GET */}
        <div className="bg-green-500 text-white p-6 rounded-2xl shadow-md">
          <p className="text-sm opacity-80">You Get</p>
          <h2 className="text-2xl font-bold mt-2">
            {formatCurrency(summary.youGet)}
          </h2>
        </div>

        {/* NET */}
        <div className="bg-blue-500 text-white p-6 rounded-2xl shadow-md">
          <p className="text-sm opacity-80">Net Balance</p>
          <h2 className="text-2xl font-bold mt-2">
            {formatCurrency(summary.netBalance)}
          </h2>
        </div>

      </div>

      {/* CHART */}
      <div className="bg-white p-5 rounded-2xl shadow-md">
        <h2 className="font-semibold mb-4">Balance Overview</h2>

        <div className="w-full h-[300px] flex items-center justify-center">

          {hasData ? (
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>

                <Pie
                  data={chartData}
                  dataKey="value"
                  nameKey="name"
                  outerRadius={100}
                  label
                >
                  {chartData.map((_, index) => (
                    <Cell key={index} fill={COLORS[index]} />
                  ))}
                </Pie>

                <Tooltip formatter={(value) => formatCurrency(value)} />

              </PieChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500">No data to display</p>
          )}

        </div>

      </div>

    </div>
  );
}