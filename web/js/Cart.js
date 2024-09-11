const loadData = async() => {
    try {
        const response = await fetch("LoadCart");

        if (response.ok) {
            const data = await response.json();

            if (data.length === 0) {
                document.getElementById("empty-cart").classList.remove("d-none");
                document.getElementById("cart-form").classList.add("d-none");
            } else {
                const tableBody = document.getElementById("cart-table-body");
                const cartItemRow = document.getElementById("cart-item-row");
                tableBody.innerHTML = "";

                let totalQty = 0;
                let total = 0;

                data.forEach(item => {
                    const itemSubTotal = item.product.price * item.qty + item.product.shipping;
                    totalQty += item.qty;
                    total += itemSubTotal;

                    const cartItemRowClone = cartItemRow.cloneNode(true);
                    cartItemRowClone.querySelector("#cart-item-remove")
                            .addEventListener(
                                    "click",
                                    (e) => {
                                removeFromCart(item.product.id);
                                e.preventDefault();
                            });
                    cartItemRowClone.querySelector("#cart-item-a").href = "productView.html?id=" + item.product.id;
                    cartItemRowClone.querySelector("#cart-item-title").href = "productView.html?id=" + item.product.id;
                    cartItemRowClone.querySelector("#cart-item-image").src = "images/product/" + item.product.id + "/" + item.product.id + "image1.png";
                    cartItemRowClone.querySelector("#cart-item-title").innerHTML = item.product.title;
                    cartItemRowClone.querySelector("#cart-item-price").innerHTML = "LKR " +
                            new Intl.NumberFormat(
                                    "en-US",
                                    {
                                        minimumFractionDigits: 2
                                    }

                            ).format(item.product.price + item.product.shipping);

                    cartItemRowClone.querySelector("#cart-item-qty").value = item.qty;
                    cartItemRowClone.querySelector("#cart-item-subtotal").innerHTML = "LKR " +
                            new Intl.NumberFormat(
                                    "en-US",
                                    {
                                        minimumFractionDigits: 2
                                    }

                            ).format((itemSubTotal));
                    tableBody.appendChild(cartItemRowClone);
                });

                document.getElementById("empty-cart").classList.add("d-none");
                document.getElementById("cart-form").classList.remove("d-none");

                document.getElementById("cart-total-qty").innerHTML = totalQty;
                document.getElementById("cart-total").innerHTML = "LKR " +
                        new Intl.NumberFormat(
                                "en-US",
                                {
                                    minimumFractionDigits: 2
                                }

                        ).format((total));
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const removeFromCart = async(id) => {
    try {
        const response = await fetch("RemoveFromCart?id=" + id);

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "Product removed from  cart!",
                    icon: "success"
                });
                loadData();
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Fetch failed:", e);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};