const loadData = async() => {
    const parameters = new URLSearchParams(window.location.search);
    if (parameters.has("id")) {
        const productId = parameters.get("id");

        try {
            const response = await fetch("LoadProductViewData?id=" + productId);

            if (response.ok) {
                const data = await response.json();

                if (data.error === "No product") {
                    window.location.href = "index.html";
                } else {
                    const id = data.product.id;

                    document.getElementById("lg-img-1").src = "images/product/" + id + "/" + id + "image1.png";
                    document.getElementById("lg-img-2").src = "images/product/" + id + "/" + id + "image2.png";
                    document.getElementById("lg-img-3").src = "images/product/" + id + "/" + id + "image3.png";

                    document.getElementById("sm-img-1").src = "images/product/" + id + "/" + id + "image1.png";
                    document.getElementById("sm-img-2").src = "images/product/" + id + "/" + id + "image2.png";
                    document.getElementById("sm-img-3").src = "images/product/" + id + "/" + id + "image3.png";

                    document.getElementById("title").innerHTML = data.product.title + " - " + data.product.color.name + " Colour";
                    document.getElementById("added-date").innerHTML = data.product.date_time;
                    document.getElementById("price").innerHTML = "LKR " +
                            new Intl.NumberFormat(
                                    "en-US",
                                    {
                                        minimumFractionDigits: 2
                                    }

                            ).format(data.product.price);

                    document.getElementById("shipping").innerHTML = "Shipping Fee : LKR " + new Intl.NumberFormat(
                            "en-US",
                            {
                                minimumFractionDigits: 2
                            }

                    ).format(data.product.shipping);
                    document.getElementById("category").innerHTML = "Category : " + data.product.model.brand.category.name;
                    document.getElementById("brand").innerHTML = "Brand : " + data.product.model.brand.name;
                    document.getElementById("model").innerHTML = "Model : " + data.product.model.name;
                    document.getElementById("condition").innerHTML = "Condition : " + data.product.productCondition.name;
                    document.getElementById("qty").innerHTML = data.product.qty > 0 ? data.product.qty + ' Items Left' : 'Out of Stock';
                    document.getElementById("qty").style.color = data.product.qty < 1 && 'Red';
                    document.getElementById("add-to-cart-btn").disabled = data.product.qty > 0 ? false : true;
                    document.getElementById("add-to-cart-btn").style.opacity = data.product.qty > 0 ? '1' : '0.5';
                    document.getElementById("add-to-cart-btn")
                            .addEventListener(
                                    "click",
                                    (e) => {
                                addToCart(
                                        data.product.id,
                                        document.getElementById("add-to-cart-qty").value
                                        );
                                e.preventDefault();
                            });

                    document.getElementById("descriptions").innerHTML = data.product.description;

                    let productHtml = document.getElementById("similer-product");
                    document.getElementById("similer-product-main").innerHTML = "";

                    data.productList.forEach(item => {
                        let productCloneHtml = productHtml.cloneNode(true);

                        productCloneHtml.querySelector("#similer-product-a1").href = "productView.html?id=" + item.id;
                        productCloneHtml.querySelector("#similer-product-image").src = "images/product/" + item.id + "/" + item.id + "image1.png";
                        productCloneHtml.querySelector("#similer-product-title").href = "productView.html?id=" + item.id;
                        productCloneHtml.querySelector("#similer-product-condition").innerHTML = item.productCondition.name;
                        productCloneHtml.querySelector("#similer-product-title").innerHTML = item.title + " - " + item.color.name;
                        productCloneHtml.querySelector("#similer-product-price").innerHTML = "LKR " +
                                new Intl.NumberFormat(
                                        "en-US",
                                        {
                                            minimumFractionDigits: 2
                                        }
                                ).format(item.price);
                        productCloneHtml.querySelector("#similer-product-qty").innerHTML = item.qty > 0 ? item.qty + ' Items Left' : 'Out of Stock';
                        productCloneHtml.querySelector("#similer-product-qty").style.color = item.qty < 1 && 'Red';
                        productCloneHtml.querySelector("#similer-product-add-to-cart").disabled = item.qty > 0 ? false : true;
                        productCloneHtml.querySelector("#similer-product-add-to-cart").style.opacity = item.qty > 0 ? '1' : '0.5';
                        productCloneHtml.querySelector("#similer-product-add-to-cart")
                                .addEventListener(
                                        "click",
                                        (e) => {
                                    addToCart(item.id, 1);
                                    e.preventDefault();
                                });

                        document.getElementById("similer-product-main").appendChild(productCloneHtml);
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
                }
            } else {
                console.error("Network error:", response.statusText);
            }
        } catch (e) {
            console.error("Fetch failed:", e);
        }
    } else {
        window.location.href = "index.html";
    }
};