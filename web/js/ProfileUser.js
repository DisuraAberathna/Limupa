var brandList;
var modelList;

const loadData = async() => {
    try {
        const response = await fetch("UserLoadData");

        if (response.ok) {
            const data = await response.json();

            loadUserData(data.user);

            const categoryList = data.categoryList;
            brandList = data.brandList;
            modelList = data.modelList;
            const colorList = data.colorList;
            const conditionList = data.conditionList;

            loadSelectOptions("category-select", categoryList, ["id", "name", "status"]);
            loadSelectOptions("brand-select", brandList, ["id", "name", "status"]);
            loadSelectOptions("model-select", modelList, ["id", "name", "status"]);
            loadSelectOptions("color-select", colorList, ["id", "name"]);
            loadSelectOptions("condition-select", conditionList, ["id", "name"]);
            loadAddress();
            updateMyProductView(data);
            updateBuyProductView(data);
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const loadUserData = (user) => {
    document.getElementById("f_name").value = user.f_name;
    document.getElementById("l_name").value = user.l_name;
    document.getElementById("email").value = user.email;
};

const loadSelectOptions = (selectTagId, list, propertyArray) => {
    const selectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item[propertyArray[0]];
        optionTag.innerHTML = item[propertyArray[1]];
        selectTag.appendChild(optionTag);    
    });
};

const loadAddress = async() => {
    try {
        const response = await fetch("LoadAddress");
        if (response.ok) {
            const data = await response.json();
            let addressHtml = document.getElementById("address-view");
            document.getElementById("address-view-main").innerHTML = "";
            data.addressList.forEach(address => {
                let addressCloneHtml = addressHtml.cloneNode(true);
                addressCloneHtml.querySelector("#is-used").checked = address.status === 1 ? true : false;
                addressCloneHtml.querySelector("#added-name").innerHTML = address.user.f_name + " " + address.user.l_name;
                addressCloneHtml.querySelector("#added-address").innerHTML = address.line_1 + ", " + address.line_2 + ", " + address.city.name + " - " + address.postal_code;
                addressCloneHtml.querySelector("#added-mobile").innerHTML = address.mobile;
                document.getElementById("address-view-main").appendChild(addressCloneHtml);
            });
            const selectTag = document.getElementById("city-select");
            data.cityList.forEach(item => {
                let optionTag = document.createElement("option");
                optionTag.value = item.id;
                optionTag.innerHTML = item.name;
                selectTag.appendChild(optionTag);    
            });
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const updateAccount = async() => {
    const reqObject = {
        f_name: document.getElementById("f_name").value,
        l_name: document.getElementById("l_name").value,
        email: document.getElementById("email").value
    };

    try {
        const response = await fetch(
                "UserAccountUpdate",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(reqObject)
                }
        );

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                if (data.msg === "Verify your email") {
                    Swal.fire({
                        title: "Information",
                        text: "You are successfully updated account details! Please check your inbox to verify your email.",
                        icon: "success"
                    });
                    setTimeout(() => {
                        window.location.href = "userVerify.html";
                    }, 3000);
                } else {
                    Swal.fire({
                        title: "Information",
                        text: "You are successfully updated account details!",
                        icon: "success"
                    });
                }
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const updateSecurity = async() => {
    const reqObject = {
        password: document.getElementById("password").value,
        confirm_password: document.getElementById("confirm_password").value
    };

    try {
        const response = await fetch(
                "UserSecurityUpdate",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(reqObject)
                }
        );

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                if (data.msg === "Verify") {
                    setTimeout(() => {
                        window.location.href = "userVerify.html";
                    }, 3000);
                } else {
                    Swal.fire({
                        title: "Information",
                        text: "You are successfully updated security details!",
                        icon: "success"
                    });
                }
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const updateBrands = () => {
    const brandTag = document.getElementById("brand-select");
    const categoryId = document.getElementById("category-select").value;
    brandTag.length = 1;

    brandList.forEach(brand => {
        if (brand.category.id == categoryId) {
            let optionTag = document.createElement("option");
            optionTag.value = brand.id;
            optionTag.innerHTML = brand.name;
            brandTag.appendChild(optionTag);
        } else if (categoryId == 0) {
            let optionTag = document.createElement("option");
            optionTag.value = brand.id;
            optionTag.innerHTML = brand.name;
            brandTag.appendChild(optionTag);
        }

    });
};

const updateModels = () => {
    const modelTag = document.getElementById("model-select");
    const brandId = document.getElementById("brand-select").value;
    modelTag.length = 1;

    modelList.forEach(model => {
        if (model.brand.id == brandId) {
            let optionTag = document.createElement("option");
            optionTag.value = model.id;
            optionTag.innerHTML = model.name;
            modelTag.appendChild(optionTag);
        } else if (brandId == 0) {
            let optionTag = document.createElement("option");
            optionTag.value = model.id;
            optionTag.innerHTML = model.name;
            modelTag.appendChild(optionTag);
        }

    });
};

const loadImage1 = () => {
    const file = document.getElementById("image-1");

    const count = file.files.length;
    if (count === 1) {
        const image = file.files[0];
        document.getElementById("image-1-view").src = URL.createObjectURL(image);
    }
};

const loadImage2 = () => {
    const file = document.getElementById("image-2");

    const count = file.files.length;
    if (count === 1) {
        const image = file.files[0];
        document.getElementById("image-2-view").src = URL.createObjectURL(image);
    }
};

const loadImage3 = () => {
    const file = document.getElementById("image-3");

    const count = file.files.length;
    if (count === 1) {
        const image = file.files[0];
        document.getElementById("image-3-view").src = URL.createObjectURL(image);
    }
};

const addProduct = async() => {
    const category = document.getElementById("category-select");
    const brand = document.getElementById("brand-select");
    const model = document.getElementById("model-select");
    const title = document.getElementById("title");
    const description = document.getElementById("description");
    const condition = document.getElementById("condition-select");
    const color = document.getElementById("color-select");
    const price = document.getElementById("price");
    const shipping = document.getElementById("shipping");
    const qty = document.getElementById("qty");
    const image_1 = document.getElementById("image-1");
    const image_2 = document.getElementById("image-2");
    const image_3 = document.getElementById("image-3");

    const form = new FormData();
    form.append("category", category.value);
    form.append("brand", brand.value);
    form.append("model", model.value);
    form.append("title", title.value);
    form.append("description", description.value);
    form.append("condition", condition.value);
    form.append("color", color.value);
    form.append("price", price.value);
    form.append("shipping", shipping.value);
    form.append("qty", qty.value);
    form.append("image_1", image_1.files[0]);
    form.append("image_2", image_2.files[0]);
    form.append("image_3", image_3.files[0]);

    try {
        const response = await fetch(
                "AddProduct",
                {
                    method: "POST",
                    body: form
                }
        );
        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: data.msg,
                    icon: "success"
                });

                category.value = 0;
                brand.value = 0;
                model.value = 0;
                title.value = "";
                description.value = "";
                condition.value = "0";
                color.value = "0";
                price.value = "";
                shipping.value = "";
                qty.value = "";
                image_1.value = null;
                document.getElementById("image-1-view").src = "images/addImage.png";
                image_2.value = null;
                document.getElementById("image-2-view").src = "images/addImage.png";
                image_3.value = null;
                document.getElementById("image-3-view").src = "images/addImage.png";
                updateBrands();
                updateModels();
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const addAddress = async() => {
    const line_1 = document.getElementById("line_1");
    const line_2 = document.getElementById("line_2");
    const city = document.getElementById("city-select");
    const postal_code = document.getElementById("postal_code");
    const mobile = document.getElementById("mobile");

    const reqObject = {
        line_1: line_1.value,
        line_2: line_2.value,
        city: city.value,
        postal_code: postal_code.value,
        mobile: mobile.value
    };
    try {
        const response = await fetch(
                "AddAddress",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(reqObject)
                }
        );
        if (response.ok) {
            const data = await response.json();
            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "Shipping address changed!",
                    icon: "success"
                });

                line_1.value = "";
                line_2.value = "";
                city.value = 0;
                postal_code.value = "";
                mobile.value = "";
                loadAddress();
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const loadMyProducts = async(firstResult) => {
    const sort = document.getElementById("my-products-sort-select").value;

    const reqObject = {
        firstResult: firstResult,
        sort: sort
    };

    try {
        const response = await fetch(
                "LoadMyProducts",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(reqObject)
                });

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                updateMyProductView(data);
            } else {
                Swal.fire({
                    title: "Warning",
                    text: "Something went wrong! Please try again later.",
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

var myProductHtml = document.getElementById("my-products-table-row");
var myProductCurrentPage = 0;
var myProductPaginationItem = document.getElementById("my-products-pagination-item");

const updateMyProductView = (data) => {
    document.getElementById("my-products-table-body").innerHTML = "";
    let row = 0;
    data.productList.forEach(product => {
        let productCloneHtml = myProductHtml.cloneNode(true);
        row++;
        productCloneHtml.querySelector("#my-products-id").innerHTML = row;
        productCloneHtml.querySelector("#my-products-image").src = "images/product/" + product.id + "/" + product.id + "image1.png";
        productCloneHtml.querySelector("#my-products-title").innerHTML = product.title + " - " + product.color.name;
        productCloneHtml.querySelector("#my-products-price").innerHTML = "LKR " +
                new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(product.price);
        productCloneHtml.querySelector("#my-products-qty").innerHTML = product.qty > 0 ? product.qty + ' Items Left' : 'Out of Stock';
        productCloneHtml.querySelector("#my-products-qty").style.color = product.qty < 1 && 'Red';
        productCloneHtml.querySelector("#my-products-status").innerHTML = product.status === 1 ? 'Active' : 'Inactive';
        document.getElementById("my-products-table-body").appendChild(productCloneHtml);
    });

    const paginationHtml = document.getElementById("my-products-pagination");
    paginationHtml.innerHTML = "";

    const productCount = data.allProductCount;
    const ProductPerPage = 10;

    const pages = Math.ceil(productCount / ProductPerPage);

    if (myProductCurrentPage !== 0) {
        const paginattionCloneItemPrev = myProductPaginationItem.cloneNode(true);
        paginattionCloneItemPrev.querySelector("#my-products-pagination-item-text").innerHTML = `<i class="fa fa-chevron-left"></i> Previous`;
        paginattionCloneItemPrev.querySelector("#my-products-pagination-item-text").classList.add("Previous");
        paginattionCloneItemPrev.querySelector("#my-products-pagination-item-text").addEventListener(
                "click", (e) => {
            myProductCurrentPage--;
            loadMyProducts(myProductCurrentPage * 10);
            e.preventDefault();
        });
        paginationHtml.appendChild(paginattionCloneItemPrev);
    }

    for (let i = 0; i < pages; i++) {
        let paginattionCloneItem = myProductPaginationItem.cloneNode(true);
        paginattionCloneItem.querySelector("#my-products-pagination-item-text").innerHTML = i + 1;

        ((i) => {
            paginattionCloneItem.querySelector("#my-products-pagination-item-text").addEventListener(
                    "click", (e) => {
                myProductCurrentPage = i;
                loadMyProducts(i * ProductPerPage);
                e.preventDefault();
            });
        })(i);

        if (i === myProductCurrentPage) {
            paginattionCloneItem.className = "active";
        }

        paginationHtml.appendChild(paginattionCloneItem);
    }

    if (myProductCurrentPage !== (pages - 1)) {
        const paginattionCloneItemNext = myProductPaginationItem.cloneNode(true);
        paginattionCloneItemNext.querySelector("#my-products-pagination-item-text").innerHTML = `Next <i class="fa fa-chevron-right"></i>`;
        paginattionCloneItemNext.querySelector("#my-products-pagination-item-text").classList.add("Next");
        paginattionCloneItemNext.querySelector("#my-products-pagination-item-text").addEventListener(
                "click", (e) => {
            myProductCurrentPage++;
            loadMyProducts(myProductCurrentPage * 10);
            e.preventDefault();
        });
        paginationHtml.appendChild(paginattionCloneItemNext);
    }
};

const loadBuyProducts = async(firstResult) => {
    const sort = document.getElementById("buy-products-sort-select").value;

    const reqObject = {
        firstResult: firstResult,
        sort: sort
    };

    try {
        const response = await fetch(
                "LoadBuyProducts",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(reqObject)
                });

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                updateBuyProductView(data);
            } else {
                Swal.fire({
                    title: "Warning",
                    text: "Something went wrong! Please try again later.",
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

var buyProductHtml = document.getElementById("buy-products-table-row");
var buyProductCurrentPage = 0;
var buyProductPaginationItem = document.getElementById("buy-products-pagination-item");

const updateBuyProductView = (data) => {
    document.getElementById("buy-products-table-body").innerHTML = "";
    let row = 0;
    data.orderList.forEach(order => {
        let productCloneHtml = buyProductHtml.cloneNode(true);
        row++;
        productCloneHtml.querySelector("#buy-products-id").innerHTML = row;
        productCloneHtml.querySelector("#buy-products-image").src = "images/product/" + order.product.id + "/" + order.product.id + "image1.png";
        productCloneHtml.querySelector("#buy-products-title").innerHTML = order.product.title + " - " + order.product.color.name;
        productCloneHtml.querySelector("#buy-products-date").innerHTML = order.order.date_time;
        productCloneHtml.querySelector("#buy-products-order-id").innerHTML = "Order Id : " + order.order.id;
        productCloneHtml.querySelector("#buy-products-price").innerHTML = "LKR " +
                new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(order.product.price);
        productCloneHtml.querySelector("#buy-products-qty").innerHTML = order.qty + " Items";
        productCloneHtml.querySelector("#buy-products-status").innerHTML = order.orderStatus.name;
        productCloneHtml.querySelector("#buy-products-send-mail")
                .addEventListener(
                        "click",
                        (e) => {
                    sendMail(order.order.id);
                    e.preventDefault();
                });
        document.getElementById("buy-products-table-body").appendChild(productCloneHtml);
    });

    const paginationHtml = document.getElementById("buy-products-pagination");
    paginationHtml.innerHTML = "";

    const productCount = data.allOrderCount;
    const ProductPerPage = 10;

    const pages = Math.ceil(productCount / ProductPerPage);

    if (buyProductCurrentPage !== 0) {
        const paginattionCloneItemPrev = buyProductPaginationItem.cloneNode(true);
        paginattionCloneItemPrev.querySelector("#buy-products-pagination-item-text").innerHTML = `<i class="fa fa-chevron-left"></i> Previous`;
        paginattionCloneItemPrev.querySelector("#buy-products-pagination-item-text").classList.add("Previous");
        paginattionCloneItemPrev.querySelector("#buy-products-pagination-item-text").addEventListener(
                "click", (e) => {
            buyProductCurrentPage--;
            loadBuyProducts(buyProductCurrentPage * 10);
            e.preventDefault();
        });
        paginationHtml.appendChild(paginattionCloneItemPrev);
    }

    for (let i = 0; i < pages; i++) {
        let paginattionCloneItem = buyProductPaginationItem.cloneNode(true);
        paginattionCloneItem.querySelector("#buy-products-pagination-item-text").innerHTML = i + 1;

        ((i) => {
            paginattionCloneItem.querySelector("#buy-products-pagination-item-text").addEventListener(
                    "click", (e) => {
                buyProductCurrentPage = i;
                loadBuyProducts(i * ProductPerPage);
                e.preventDefault();
            });
        })(i);

        if (i === buyProductCurrentPage) {
            paginattionCloneItem.className = "active";
        }

        paginationHtml.appendChild(paginattionCloneItem);
    }

    if (buyProductCurrentPage !== (pages - 1)) {
        const paginattionCloneItemNext = buyProductPaginationItem.cloneNode(true);
        paginattionCloneItemNext.querySelector("#buy-products-pagination-item-text").innerHTML = `Next <i class="fa fa-chevron-right"></i>`;
        paginattionCloneItemNext.querySelector("#buy-products-pagination-item-text").classList.add("Next");
        paginattionCloneItemNext.querySelector("#buy-products-pagination-item-text").addEventListener(
                "click", (e) => {
            buyProductCurrentPage++;
            loadBuyProducts(buyProductCurrentPage * 10);
            e.preventDefault();
        });
        paginationHtml.appendChild(paginattionCloneItemNext);
    }
};

const sendMail = async(id) => {
    console.log("sendMail: " + id);
    try {
        const response = await fetch("SendMail?id=" + id);

        if (response.ok) {
            const data = await response.json();

            if (data.ok) {
                Swal.fire({
                    title: "Information",
                    text: "Payment completed! Your invoice send to your email.",
                    icon: "success"
                });
            } else {
                Swal.fire({
                    title: "Warning",
                    text: data.msg,
                    icon: "warning"
                });
            }
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};