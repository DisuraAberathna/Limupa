const loadProduct = async() => {
    try {
        const response = await fetch("LoadProducts");

        if (response.ok) {
            const data = await response.json();

            let categoryHtml = document.getElementById("category-section");
            document.getElementById("category-section-main").innerHTML = "";

            data.categoryList.forEach(category => {
                let categoryCloneHtml = categoryHtml.cloneNode(true);

                categoryCloneHtml.querySelector("#category-title").innerHTML = category.name;
                document.getElementById("category-section-main").appendChild(categoryCloneHtml);

                let productHtml = categoryCloneHtml.querySelector("#product");
                categoryCloneHtml.querySelector("#product-main").innerHTML = "";

                category.productList.forEach(product => {
                    let productCloneHtml = productHtml.cloneNode(true);

                    productCloneHtml.querySelector("#product-a1").href = "productView.html?id=" + product.id;
                    productCloneHtml.querySelector("#product-image").src = "images/product/" + product.id + "/" + product.id + "image1.png";
                    productCloneHtml.querySelector("#product-title").href = "productView.html?id=" + product.id;
                    productCloneHtml.querySelector("#product-condition").innerHTML = product.productCondition.name;
                    productCloneHtml.querySelector("#product-title").innerHTML = product.title + " - " + product.color.name;
                    productCloneHtml.querySelector("#product-price").innerHTML = "LKR " +
                            new Intl.NumberFormat(
                                    "en-US",
                                    {
                                        minimumFractionDigits: 2
                                    }
                            ).format(product.price);
                    productCloneHtml.querySelector("#product-qty").innerHTML = product.qty > 0 ? product.qty + ' Items Left' : 'Out of Stock';
                    productCloneHtml.querySelector("#product-qty").style.color = product.qty < 1 && 'Red';
                    productCloneHtml.querySelector("#product-add-to-cart").disabled = product.qty > 0 ? false : true;
                    productCloneHtml.querySelector("#product-add-to-cart").style.opacity = product.qty > 0 ? '1' : '0.5';
                    productCloneHtml.querySelector("#product-add-to-cart")
                            .addEventListener(
                                    "click",
                                    (e) => {
                                addToCart(product.id, 1);
                                e.preventDefault();
                            });
                    productCloneHtml.querySelector("#product-add-to-watchlist")
                            .addEventListener(
                                    "click",
                                    (e) => {
                                addToWatchlist(product.id);
                                e.preventDefault();
                            });

                    categoryCloneHtml.querySelector("#product-main").appendChild(productCloneHtml);
                });
            });
            $(".product-active").owlCarousel({
                loop: true,
                nav: true,
                dots: false,
                autoplay: false,
                autoplayTimeout: 5000,
                navText: ["<i class='fa fa-angle-left'></i>", "<i class='fa fa-angle-right'></i>"],
                item: 5,
                responsive: {
                    0: {
                        items: 1
                    },
                    480: {
                        items: 2
                    },
                    768: {
                        items: 3
                    },
                    992: {
                        items: 4
                    },
                    1200: {
                        items: 5
                    }
                }
            });
        } else {
            console.error("Fetch failed:", e);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};