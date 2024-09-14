const loadData = async() => {
    try {
        const response = await fetch("LoadWatchlist");

        if (response.ok) {
            const data = await response.json();

            if (data.length === 0) {
                document.getElementById("empty-watchlist").classList.remove("d-none");
                document.getElementById("watchlist-form").classList.add("d-none");
            } else {
                const tableBody = document.getElementById("watchlist-table-body");
                const cartItemRow = document.getElementById("watchlist-item-row");
                tableBody.innerHTML = "";

                data.forEach(item => {
                    const cartItemRowClone = cartItemRow.cloneNode(true);
                    cartItemRowClone.querySelector("#watchlist-item-remove")
                            .addEventListener(
                                    "click",
                                    (e) => {
                                removeFromWatchlist(item.product.id);
                                e.preventDefault();
                            });
                    cartItemRowClone.querySelector("#watchlist-item-a").href = "productView.html?id=" + item.product.id;
                    cartItemRowClone.querySelector("#watchlist-item-title").href = "productView.html?id=" + item.product.id;
                    cartItemRowClone.querySelector("#watchlist-item-image").src = "images/product/" + item.product.id + "/" + item.product.id + "image1.png";
                    cartItemRowClone.querySelector("#watchlist-item-title").innerHTML = item.product.title;
                   
                    tableBody.appendChild(cartItemRowClone);
                });

                document.getElementById("empty-watchlist").classList.add("d-none");
                document.getElementById("watchlist-form").classList.remove("d-none");
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const removeFromWatchlist = async(id) => {
    try {
        const response = await fetch("RemoveFromWatchlist?id=" + id);

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