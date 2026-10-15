import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { foodmeApi } from "@/api/foodme";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
} from "@/components/ui/dialog";
import { StarRatingInput } from "@/components/sections/star-rating";
import type { FullOrderDto, OrderListResponseDto } from "@/types";

const COMMENT_MAX = 1000;

export function RateOrderDialog({
  order,
  open,
  onOpenChange,
}: {
  order: FullOrderDto;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const queryClient = useQueryClient();
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");

  const mutation = useMutation({
    mutationFn: () =>
      foodmeApi.reviewOrder(order.number, {
        rating,
        comment: comment.trim() ? comment.trim() : undefined,
      }),
    onSuccess: (updated) => {
      // Show the stars in place of the button right away, then refetch in the background.
      queryClient.setQueriesData<OrderListResponseDto>({ queryKey: ["my-orders"] }, (old) =>
        old
          ? { ...old, list: old.list.map((o) => (o.number === updated.number ? updated : o)) }
          : old,
      );
      queryClient.setQueryData(["order", updated.number], updated);
      void queryClient.invalidateQueries({ queryKey: ["my-orders"] });
      // The chef's average changed: refresh chef cards and the chef page.
      void queryClient.invalidateQueries({ queryKey: ["chef", String(order.chefId)] });
      void queryClient.invalidateQueries({ queryKey: ["chefs"] });
      onOpenChange(false);
    },
  });

  function handleOpenChange(next: boolean) {
    if (!next) {
      setRating(0);
      setComment("");
      mutation.reset();
    }
    onOpenChange(next);
  }

  const errorMessage =
    mutation.error instanceof Error && mutation.error.message
      ? mutation.error.message
      : "Couldn’t save your rating. Please try again.";

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="p-5 sm:p-6">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            if (rating > 0 && !mutation.isPending) mutation.mutate();
          }}
        >
          <DialogTitle className="pr-10 font-display text-xl font-extrabold text-zinc-900">
            Rate order {order.number}
          </DialogTitle>
          <DialogDescription className="mt-1 text-sm text-zinc-500">
            How was your food from {order.chefName}?
          </DialogDescription>

          <div className="mt-5">
            <StarRatingInput
              name={`rating-${order.number}`}
              value={rating}
              onChange={setRating}
              disabled={mutation.isPending}
            />
          </div>

          <label htmlFor={`comment-${order.number}`} className="mt-5 block text-sm font-semibold text-zinc-900">
            Comment (optional)
          </label>
          <textarea
            id={`comment-${order.number}`}
            className={[
              "mt-2 w-full resize-none rounded-xl border border-zinc-200 bg-white px-4 py-2.5 text-sm text-zinc-900",
              "placeholder:text-zinc-400",
              "shadow-[0_1px_3px_-1px_rgba(0,0,0,0.05)]",
              "transition-all duration-200 ease-[cubic-bezier(0.32,0.72,0,1)]",
              "hover:border-zinc-300",
              "focus-visible:outline-none focus-visible:border-zinc-400 focus-visible:ring-4 focus-visible:ring-zinc-100",
            ].join(" ")}
            rows={4}
            maxLength={COMMENT_MAX}
            placeholder="Tell us about your order"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            disabled={mutation.isPending}
            aria-describedby={`comment-count-${order.number}`}
          />
          <p id={`comment-count-${order.number}`} className="mt-1 text-right text-xs tabular-nums text-zinc-400">
            {comment.length}/{COMMENT_MAX}
          </p>

          {mutation.isError && (
            <div
              role="alert"
              className="mt-3 rounded-xl border border-red-100 bg-red-50 px-4 py-3 text-sm text-red-700"
            >
              {errorMessage}
            </div>
          )}

          <div className="mt-5 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
            <Button type="button" variant="outline" onClick={() => handleOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={rating === 0 || mutation.isPending}>
              {mutation.isPending ? "Submitting…" : mutation.isError ? "Try again" : "Submit rating"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
