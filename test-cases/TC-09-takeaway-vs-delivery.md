# TC-09: Choosing Takeaway or Delivery at checkout

**Priority:** P1 | **Type:** Acceptance / Functional | **User:** Registered user

## Objective
Switching between **Delivery** and **Takeaway** correctly shows or hides the address form, recalculates the delivery fee and sends the right data to the server.

## Preconditions
- Signed in. Cart subtotal is **below** 5,000 AMD (e.g. Mushroom soup ×1 = 1,950 AMD from `/chef/17`)
- The chef supports both `DELIVERY` and `TAKEAWAY`
- ⚠️ Step 6 creates a real order. Use a QA account and clean up afterwards

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/checkout` | **Delivery method** shows "Delivery – To your door" (selected) and "Takeaway – Pick up". The **Delivery address** section (City, Street, Building, Apartment) is visible |
| 2 | Check the price summary | Delivery is **500 AMD**, there's an "Add 3,050 AMD more for free delivery." hint, and Total is **2,450 AMD** |
| 3 | Select **Takeaway** | The address section is hidden. Delivery is **0 / Free**. The free-delivery hint disappears. Total equals Subtotal (1,950 AMD) |
| 4 | With Takeaway selected and City/Street empty, click **Place order** | No "City/Street is required" error appears, because the address isn't needed for takeaway |
| 5 | Switch back to **Delivery** with City/Street empty and click **Place order** | Blocked with **"City is required"** and **"Street is required"**. Delivery fee is 500 AMD again |
| 6 | Select Takeaway and place the order | `POST /api/order` has `"deliveryMethod":"TAKEAWAY"` and `"addressDto":null`. The confirmed total doesn't include a delivery fee |
| 7 | (If a chef offers only one method) open checkout for that chef | Only the allowed method is shown and pre-selected |

## Pass criteria
The fee, address requirement and API payload always match the selected method.

> API check on prod 2026-10-06: `POST /api/order/delivery-price` with subtotal 1,950 returned 500 for DELIVERY and 0 for TAKEAWAY (freeDeliveryFrom 5,000).
