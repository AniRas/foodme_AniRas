import { useState } from "react";
import { Star } from "lucide-react";
import { cn } from "@/lib/utils";

const STARS = [1, 2, 3, 4, 5] as const;

const STAR_LABEL: Record<number, string> = {
  1: "1 star, poor",
  2: "2 stars, fair",
  3: "3 stars, good",
  4: "4 stars, very good",
  5: "5 stars, excellent",
};

/** Read-only stars; screen readers hear "Rated N out of 5 stars". */
export function StarRatingDisplay({
  rating,
  size = 16,
  className,
}: {
  rating: number;
  size?: number;
  className?: string;
}) {
  return (
    <span
      role="img"
      aria-label={`Rated ${rating} out of 5 stars`}
      className={cn("inline-flex items-center gap-0.5", className)}
    >
      {STARS.map((value) => (
        <Star
          key={value}
          size={size}
          strokeWidth={1.75}
          aria-hidden="true"
          className={value <= rating ? "fill-amber-400 text-amber-400" : "fill-zinc-100 text-zinc-300"}
        />
      ))}
    </span>
  );
}

/**
 * Star picker built on a native radio group: Tab focuses it, arrow keys change the
 * value, tap/click selects, and each star is announced with its label.
 */
export function StarRatingInput({
  name,
  value,
  onChange,
  disabled,
}: {
  name: string;
  value: number;
  onChange: (value: number) => void;
  disabled?: boolean;
}) {
  const [hovered, setHovered] = useState(0);
  const shown = hovered || value;

  return (
    <fieldset disabled={disabled} className="min-w-0">
      <legend className="sr-only">Your rating</legend>
      <div className="flex items-center gap-1" onMouseLeave={() => setHovered(0)}>
        {STARS.map((star) => (
          <label
            key={star}
            onMouseEnter={() => setHovered(star)}
            className={cn(
              "flex h-11 w-11 cursor-pointer items-center justify-center rounded-full",
              "transition-transform duration-150 ease-[cubic-bezier(0.32,0.72,0,1)] active:scale-90",
              "has-[:focus-visible]:ring-2 has-[:focus-visible]:ring-zinc-900 has-[:focus-visible]:ring-offset-2",
              disabled && "cursor-not-allowed opacity-60",
            )}
          >
            <input
              type="radio"
              name={name}
              value={star}
              checked={value === star}
              onChange={() => onChange(star)}
              className="sr-only"
              aria-label={STAR_LABEL[star]}
            />
            <Star
              size={30}
              strokeWidth={1.5}
              aria-hidden="true"
              className={star <= shown ? "fill-amber-400 text-amber-400" : "fill-white text-zinc-300"}
            />
          </label>
        ))}
      </div>
    </fieldset>
  );
}
