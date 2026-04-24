import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../services/api";

export default function GroupDetails() {

  const { id } = useParams();

  const [group, setGroup] = useState(null);
  const [expenses, setExpenses] = useState([]);
  const [balances, setBalances] = useState([]);
  const [settlements, setSettlements] = useState([]);

  const [amount, setAmount] = useState("");
  const [description, setDescription] = useState("");

  const [splitType, setSplitType] = useState("EQUAL");
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [customSplits, setCustomSplits] = useState({});
  const [percentSplits, setPercentSplits] = useState({});

  const [paidBy, setPaidBy] = useState(null);

  // ================= FETCH =================

  const fetchData = async () => {
    try {
      const [g, e, b, s] = await Promise.all([
        api.get(`/groups/${id}`),
        api.get(`/expenses/group/${id}`),
        api.get(`/balances/group/${id}`), // ✅ FIXED
        api.get(`/settlements/${id}`)     // ✅ FIXED
      ]);

      // ✅ remove duplicate members
      const uniqueMembers = Array.from(
        new Map(g.data.members.map(m => [m.id, m])).values()
      );

      setGroup({ ...g.data, members: uniqueMembers });
      setExpenses(e.data);

      // ✅ clean balances
      const cleanBalances = b.data.filter(
        x =>
          x.user1Id !== x.user2Id &&
          x.amount > 0
      );

      setBalances(cleanBalances);
      setSettlements(s.data);

    } catch (err) {
      console.error("Fetch error:", err);
    }
  };

  useEffect(() => {
    fetchData();
  }, [id]); // ✅ IMPORTANT

useEffect(() => {
  if (group) {
    setSelectedMembers(group.members.map(m => m.id));
    setPaidBy(group.members[0]?.id); // default payer
  }
}, [group]);

  // ================= HELPERS =================

  const getUserName = (id) => {
    const user = group?.members.find(m => m.id === id);
    return user ? user.name : null;
  };

  const toggleMember = (id) => {
    setSelectedMembers(prev =>
      prev.includes(id)
        ? prev.filter(m => m !== id)
        : [...prev, id]
    );
  };

  const handleCustomChange = (id, value) => {
    setCustomSplits(prev => ({ ...prev, [id]: Number(value) }));
  };

  const handlePercentChange = (id, value) => {
    setPercentSplits(prev => ({ ...prev, [id]: Number(value) }));
  };

  const round = (val) => Math.round(val * 100) / 100;

  // ================= ADD EXPENSE =================

  const addExpense = async () => {

    if (!amount || !description) return;

    if (selectedMembers.length < 2) {
      alert("Select at least 2 members");
      return;
    }

    let splits = [];

    if (splitType === "EQUAL") {
      splits = selectedMembers.map(id => ({ userId: id }));
    }

    if (splitType === "CUSTOM") {
      const total = round(
        Object.values(customSplits).reduce((a, b) => a + b, 0)
      );

      if (total !== round(Number(amount))) {
        alert("Custom total must match amount");
        return;
      }

      splits = selectedMembers.map(id => ({
        userId: id,
        amount: customSplits[id] || 0
      }));
    }

    if (splitType === "PERCENT") {
      const total = round(
        Object.values(percentSplits).reduce((a, b) => a + b, 0)
      );

      if (total !== 100) {
        alert("Percent must be 100%");
        return;
      }

      splits = selectedMembers.map(id => ({
        userId: id,
        percent: percentSplits[id] || 0
      }));
    }

    try {
      await api.post("/expenses", {
        groupId: group.id,
        amount: Number(amount),
        description,
        splitType,
        paidBy,
        splits
      });

      // reset
      setAmount("");
      setDescription("");
      setCustomSplits({});
      setPercentSplits({});

      fetchData();

    } catch (err) {
      console.error(err);
    }
  };

  // ================= SETTLE =================

  const settle = async (toUserId, amount) => {
    try {
      await api.post("/payments/settle", { toUserId, amount });
      fetchData();
    } catch (err) {
      console.error(err);
    }
  };

  if (!group) return <p className="text-center mt-10">Loading...</p>;

  // ================= UI =================

  return (
    <div className="space-y-6">

      <h1 className="text-2xl font-bold">{group.name}</h1>

      {/* ADD EXPENSE */}
      <div className="bg-white p-5 rounded-xl shadow space-y-3">

        <input
          className="border p-2 w-full"
          placeholder="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <input
          className="border p-2 w-full"
          type="number"
          placeholder="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        {/* PAID BY */}
        <div>
        <label className="block mb-1 font-medium">Paid By</label>

        <select
            className="border p-2 w-full"
            value={paidBy || ""}
            onChange={(e) => setPaidBy(Number(e.target.value))}
        >
            {group.members.map(user => (
            <option key={user.id} value={user.id}>
                {user.name}
            </option>
            ))}
        </select>
        </div>

        <select
          className="border p-2 w-full"
          value={splitType}
          onChange={(e) => setSplitType(e.target.value)}
        >
          <option value="EQUAL">Equal</option>
          <option value="CUSTOM">Custom</option>
          <option value="PERCENT">Percent</option>
        </select>

        {/* MEMBERS */}
        {group.members.map(m => (
          <div key={m.id} className="flex justify-between items-center">

            <label className="flex gap-2">
              <input
                type="checkbox"
                checked={selectedMembers.includes(m.id)}
                onChange={() => toggleMember(m.id)}
              />
              {m.name}
            </label>

            {splitType === "CUSTOM" && selectedMembers.includes(m.id) && (
              <input
                type="number"
                className="border p-1 w-20"
                onChange={(e) => handleCustomChange(m.id, e.target.value)}
              />
            )}

            {splitType === "PERCENT" && selectedMembers.includes(m.id) && (
              <input
                type="number"
                className="border p-1 w-20"
                onChange={(e) => handlePercentChange(m.id, e.target.value)}
              />
            )}

          </div>
        ))}

        <button
          className="bg-blue-600 text-white px-4 py-2 rounded"
          onClick={addExpense}
        >
          Add Expense
        </button>

      </div>

      {/* EXPENSES */}
      <div className="bg-white p-5 rounded-xl shadow">
        <h2 className="font-semibold mb-3">Expenses</h2>

        {expenses.length === 0 ? (
          <p className="text-gray-500">No expenses yet</p>
        ) : (
          expenses.map(e => (
            <div key={e.id} className="flex justify-between py-2 border-b">
              <span>{e.description}</span>
              <span>₹{e.amount}</span>
            </div>
          ))
        )}
      </div>

      {/* BALANCES */}
      <div className="bg-white p-5 rounded-xl shadow">

        <h2 className="font-semibold mb-3">Balances</h2>

        {balances.length === 0 ? (
          <p className="text-gray-500">All settled 🎉</p>
        ) : (
          balances.map((b, i) => {

            const from = getUserName(b.user1Id);
            const to = getUserName(b.user2Id);

            if (!from || !to || from === to) return null;

            return (
              <div key={i} className="flex justify-between py-2 border-b">

                <span>
                  <b>{from}</b> owes <b>{to}</b>
                </span>

                <div className="flex gap-3">
                  <span className="text-red-500">₹{b.amount}</span>

                  <button
                    onClick={() => settle(b.user2Id, b.amount)}
                    className="bg-green-500 text-white px-2 py-1 text-xs rounded"
                  >
                    Settle
                  </button>
                </div>

              </div>
            );
          })
        )}
      </div>

      {/* OPTIMIZED */}
      <div className="bg-white p-5 rounded-xl shadow">

        <h2 className="font-semibold mb-3">Optimized Settlements</h2>

        {settlements.length === 0 ? (
          <p className="text-gray-500">All settled 🎉</p>
        ) : (
          settlements.map((s, i) => {

            const from = getUserName(s.fromUserId);
            const to = getUserName(s.toUserId);

            if (!from || !to || from === to) return null;

            return (
              <div key={i} className="flex justify-between py-2 border-b">
                <span>{from} pays {to}</span>
                <span className="text-green-600">₹{s.amount}</span>
              </div>
            );
          })
        )}

      </div>

    </div>
  );
}