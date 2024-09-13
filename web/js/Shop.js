var brandList;

const loadData = async() => {
    try {
        const response = await fetch("LoadSearchData");
        if (response.ok) {
            const data = await response.json();

            const categoryList = data.categoryList;
            brandList = data.brandList;
            const colorList = data.colorList;

            loadSelectOptions("category-select", categoryList, ["id", "name", "status"]);
            loadSelectOptions("brand-select", brandList, ["id", "name", "status"]);
            loadSelectOptions("color-select", colorList, ["id", "name"]);
            loadConditions(data.conditionList);

            updateProductView(data)

        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const loadConditions = (conditions) => {
    document.getElementById("condition-section-main").innerHTML = "";
    let conditionList = "";

    conditions.forEach(condition => {
        conditionList += `<li>
                            <div class="d-flex align-items-center">
                                <input type="radio" name="condition" id="${condition.name}" class="radio" value="${condition.name}">
                                <label for="${condition.name}" style="height: 15px" class="ml-10 cursor-pointer brand-item">${condition.name}</label>
                            </div>
                         </li>`;
    });

    document.getElementById("condition-section-main").innerHTML = conditionList;
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

var productHtml = document.getElementById("product");
var currentPage = 0;
var paginationItem = document.getElementById("pagination-item");

const updateProductView = (data) => {
    document.getElementById("product-main").innerHTML = "";

    data.productList.forEach(product => {
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
        document.getElementById("product-main").appendChild(productCloneHtml);
    });

    const paginationHtml = document.getElementById("pagination");
    paginationHtml.innerHTML = "";

    const productCount = data.allProductCount;
    const ProductPerPage = 3;

    const pages = Math.ceil(productCount / ProductPerPage);

    if (currentPage != 0) {
        const paginattionCloneItemPrev = paginationItem.cloneNode(true);
        paginattionCloneItemPrev.querySelector("#pagination-item-text").innerHTML = `<i class="fa fa-chevron-left"></i> Previous`;
        paginattionCloneItemPrev.querySelector("#pagination-item-text").classList.add("Previous");
        paginattionCloneItemPrev.querySelector("#pagination-item-text").addEventListener(
                "click", e => {
                    currentPage--;
                    searchProduct(currentPage * 9);

                });
        paginationHtml.appendChild(paginattionCloneItemPrev);
    }

    for (var i = 0; i < pages; i++) {
        let paginattionCloneItem = paginationItem.cloneNode(true);
        paginattionCloneItem.querySelector("#pagination-item-text").innerHTML = i + 1;

        paginattionCloneItem.querySelector("#pagination-item-text").addEventListener(
                "click", e => {
                    currentPage = i;
                    searchProduct(i * 9);

                });

        if (i == currentPage) {
            paginattionCloneItem.className = "active";
        }

        paginationHtml.appendChild(paginattionCloneItem);
    }

    if (currentPage != (pages - 1)) {
        const paginattionCloneItemNext = paginationItem.cloneNode(true);
        paginattionCloneItemNext.querySelector("#pagination-item-text").innerHTML = `Next <i class="fa fa-chevron-right"></i>`;
        paginattionCloneItemNext.querySelector("#pagination-item-text").classList.add("Next");
        paginattionCloneItemNext.querySelector("#pagination-item-text").addEventListener(
                "click", e => {
                    currentPage++;
                    searchProduct(currentPage * 9);

                });
        paginationHtml.appendChild(paginattionCloneItemNext);
    }
};

const searchProduct = async(firstResult) => {
    const sort = document.getElementById("sort-select").value;
    const category = document.getElementById("category-select").value;
    const brand = document.getElementById("brand-select").value;
    const color = document.getElementById("color-select").value;
    let condition;
    const brandNew = document.getElementById("Brand New");
    const used = document.getElementById("Used");
    if (brandNew.checked) {
        condition = brandNew.value;
    } else if (used.checked) {
        condition = used.value;
    }
    const priceStart = $('#slider-range').slider('values', 0);
    const priceEnd = $('#slider-range').slider('values', 1);

    const reqObject = {
        firstResult: firstResult,
        sort: sort,
        category: category,
        brand: brand,
        color: color,
        condition: condition,
        priceStart: priceStart,
        priceEnd: priceEnd
    };

    try {
        const response = await fetch(
                "SearchProduct",
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
                updateProductView(data);
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

const clearSearch = () => {
    document.getElementById("category-select").value = 0;
    document.getElementById("brand-select").value = 0;
    document.getElementById("color-select").value = 0;
    document.getElementById("sort-select").value = 0;

    const radioButtons = document.querySelectorAll('input[type="radio"]');

    for (let i = 0; i < radioButtons.length; i++) {
        radioButtons[i].checked = false;
    }

    const defaultMin = 0;
    const defaultMax = 1000000;

    $("#slider-range").slider("option", "values", [defaultMin, defaultMax]);

    $("#amount").val(
            "LKR " +
            new Intl.NumberFormat("en-US", {
                minimumFractionDigits: 2
            }).format(defaultMin) +
            " - LKR " +
            new Intl.NumberFormat("en-US", {
                minimumFractionDigits: 2
            }).format(defaultMax)
            );
    searchProduct(0);
};