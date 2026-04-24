export default function Card({
  title,
  value,
  color = "bg-blue-500",
  icon = null,
  subtitle = null
}) {
  return (
    <div
      className={`
        ${color}
        text-white
        p-5
        rounded-2xl
        shadow-lg
        hover:shadow-xl
        transition
        flex flex-col justify-between
      `}
    >

      {/* Top Section */}
      <div className="flex justify-between items-center">
        <h3 className="text-sm opacity-80">{title}</h3>
        {icon && <div className="text-xl">{icon}</div>}
      </div>

      {/* Value */}
      <div className="mt-3">
        <p className="text-3xl font-bold">₹{value}</p>
        {subtitle && (
          <p className="text-xs opacity-80 mt-1">{subtitle}</p>
        )}
      </div>

    </div>
  );
}