import Link from "next/link";

interface ButtonProps {
  label: string;
  href: string;
  variant?: "primary" | "secondary" | "big";
}

export default function Button({
  label,
  href,
  variant = "primary",
}: ButtonProps) {
  const primaryStyles = "bg-[#f5f5f5] text-[#000000] px-4 py-2";
  const secondaryStyles = "bg-[#7b65be] text-[#f5f5f5] px-4 py-2";
  const bigButton =
    "bg-[#534487] text-[#f5f5f5] px-20 py-8 text-4xl font-bold ml-2 mr-2 rounded-3xl opacity-80 hover:opacity-100";

  let buttonStyles = variant === "primary" ? primaryStyles : secondaryStyles;
  buttonStyles = variant === "big" ? bigButton : secondaryStyles;

  return (
    <Link href={href}>
      <button
        className={` mr-2 ml-2 rounded-lg ${buttonStyles} hover:shadow-xl transition-all duration-200`}
      >
        {label}
      </button>
    </Link>
  );
}
