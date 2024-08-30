# Extra Tasks

Once you run the project and understand its main structure, you can start experimenting with the existing code by tackling the following tasks or exploring anything else that aligns with your current needs.

Remember to focus on the most important aspects, set your own pace, and enjoy discovering the full-stack technologies presented.

1. Adjust the picture to fit the box size.
2. Category filtering should include a category parameter to ensure the filtering persists after a page refresh.
3. Add Expiry Date to AddProductRequest.
4. Offer ID in the path causes offer ID leakage. Determine how to avoid this.
5. Test switching `show-item-tabs`. Bootstrap jQuery doesn't work (Hint: Tab switching should be implemented using the React `useState` hook).
6. Extract the `offerQuantity` button as a component to avoid code duplication.
7. Use `&nbsp;` to avoid breaking lines unnecessarily.
8. Requests that return a `403 Forbidden` code should typically return `404 Not Found` to avoid informing the user that the object exists in the system.
9. Ensure the order cannot be submitted without all required information (refer to `SubmitOrderEndpointTest`).
10. Handle refresh tokens properly when the session expires.
11. Paths in Spring Boot REST controllers are interpreted differently than in Koa (e.g., a trailing `/` causes an error in Spring but works in Koa).
12. Create a checkout page to confirm the payment method and delivery address.
13. Update Docker settings to remove hardcoded values.
14. Implement blog functionality:
    - Display some blog entries;
    - Full CRUD operations for the blog.
