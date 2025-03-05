import Link from "next/link";
import Button from "@/app/components/Button";

export default function Header() {
  const isAuthorized = true;

  return (
    <header className="sticky bg-[#534487] py-4 px-6 flex justify-between z-10">
      <div>
        <Link
          href="/"
          className="text-3xl font-bold hover:text-white transition-all duration-100"
        >
          MP
        </Link>
      </div>

      {isAuthorized && (
        <div>
          <Button label="Sing in" href="/" variant="primary" />
          <Button label="Sing up" href="/" variant="secondary" />
        </div>
      )}
    </header>
  );
}
