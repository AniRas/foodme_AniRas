# TC-04: Free-delivery threshold is the same in the cart and on the server

**Priority:** P0 | **Type:** Acceptance / Boundary | **User:** Guest

## Objective
The free-delivery rule ("free from 5,000 AMD") gives the same answer in the cart and in the server's delivery-price calculation, including at exactly 5,000 AMD. Otherwise the customer sees one total in the cart and is charged another.

## Preconditions
- Cart is empty
- Argentinean `/chef/39`: delivery 500 AMD, "free from 5,000 AMD", **Mini Corn Dog – 500 AMD**

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/chef/39` and check the header line | Shows "500 AMD delivery · free from 5,000 AMD" |
| 2 | Add **Mini Corn Dog** and increase its quantity to **9** (subtotal 4,500 AMD) | Delivery **500 AMD**, Total **5,000 AMD**, hint "Add 500 AMD more for free delivery." |
| 3 | Increase to **10** (subtotal exactly 5,000 AMD) | Delivery **Free**, Total **5,000 AMD**, no "add more" hint |
| 4 | Increase to **11** (5,500 AMD) | Delivery **Free**, Total **5,500 AMD** |
| 5 | `POST /api/order/delivery-price` with `{"chefId":39,"subtotal":4999,"deliveryMethod":"DELIVERY"}` | `200`, `deliveryPrice: 500`, `freeDeliveryFrom: 5000` |
| 6 | Same call with `"subtotal":5000` | `200`, **`deliveryPrice: 0`**, same as the cart in step 3 |
| 7 | Same call with `"subtotal":5001` | `200`, `deliveryPrice: 0` |
| 8 | Same call with `"deliveryMethod":"TAKEAWAY"` and `"subtotal":500` | `200`, `deliveryPrice: 0` |
| 9 | Remove the item | Cart is empty |

## Pass criteria
At every subtotal, the cart and the server agree on the delivery fee, and "free from 5,000 AMD" includes 5,000 itself.

> ❌ **Fails on prod 2026-10-07 (API):** step 6 returns `deliveryPrice: 500`. The server only makes delivery free *above* 5,000, while the cart treats 5,000 as free. Cart steps 2–4 not run yet.
