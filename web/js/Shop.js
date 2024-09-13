const loadData = async() => {
    try {
        const response = await fetch("LoadSearchData");
        if (response.ok) {
            const data = await response.json();

            loadBrandList(data.categoryList);
            loadColors(data.colorList);
            loadConditions(data.conditionList);

            const productHtml = document.getElementById("product");
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
            const paginationItem = document.getElementById("pagination-item");
            paginationHtml.innerHTML = "";

            const productCount = data.allProductCount;
            const ProductPerPage = 3;

            const pages = Math.ceil(productCount / ProductPerPage);

            const paginattionCloneItemPrev = paginationItem.cloneNode(true);
            paginattionCloneItemPrev.querySelector("#pagination-item-text").innerHTML = `<i class="fa fa-chevron-left"></i> Previous`;
            paginattionCloneItemPrev.querySelector("#pagination-item-text").classList.add("Previous");
            paginationHtml.appendChild(paginattionCloneItemPrev);

            for (var i = 0; i < pages; i++) {
                let paginattionCloneItem = paginationItem.cloneNode(true);
                paginattionCloneItem.querySelector("#pagination-item-text").innerHTML = i + 1;

                paginationHtml.appendChild(paginattionCloneItem);
            }

            const paginattionCloneItemNext = paginationItem.cloneNode(true);
            paginattionCloneItemNext.querySelector("#pagination-item-text").innerHTML = `Next <i class="fa fa-chevron-right"></i>`;
            paginattionCloneItemNext.querySelector("#pagination-item-text").classList.add("Next");
            paginationHtml.appendChild(paginattionCloneItemNext);
        } else {
            console.error("Network error:", response.statusText);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};

const loadBrandList = (categoryList) => {
    document.getElementById("category-section-main").innerHTML = "";
    let brandList = "";

    categoryList.forEach((category, idx) => {
        brandList += `<li>
                        <a class="d-flex justify-content-between align-items-center category" onclick="toggleBrands(${category.id})">
                            <span>
                                 ${category.name}
                            </span>
                            <span id="category-toggle-icon-${category.id}" class="category-toggle-icon">${idx === 0 ? '-' : '+'}</span>
                        </a>
                        <ul id="brands-${category.id}" style="display: ${idx === 0 ? 'block' : 'none'}" class="brands-list">
                          ${category.brandList.map(brand => `
                            <li>
                              <div class="d-flex align-items-center">
                                  <input type="radio" name="brand" id="brand-${brand.id}" class="radio" value="${brand.name}">
                                  <label for="brand-${brand.id}" style="height: 15px" class="ml-10 cursor-pointer brand-item">${brand.name}</label>
                              </div>
                            </li>
                          `).join('')}
                        </ul>
                      </li>`;
    });
    document.getElementById("category-section-main").innerHTML = brandList;
};

const loadColors = (colors) => {
    document.getElementById("color-section-main").innerHTML = "";
    let colorList = "";

    colors.forEach(color => {
        colorList += `<li>
                        <div class="d-flex align-items-center">
                            <input type="radio" name="color" id="color-${color.id}" class="radio" value="${color.name}">
                            <label for="color-${color.id}" style="height: 15px" class="ml-10 cursor-pointer brand-item">${color.name}</label>
                        </div>
                      </li>`;
    });

    document.getElementById("color-section-main").innerHTML = colorList;
};

const loadConditions = (conditions) => {
    document.getElementById("condition-section-main").innerHTML = "";
    let conditionList = "";

    conditions.forEach(condition => {
        conditionList += `<li>
                            <div class="d-flex align-items-center">
                                <input type="radio" name="condition" id="condition-${condition.id}" class="radio" value="${condition.name}">
                                <label for="condition-${condition.id}" style="height: 15px" class="ml-10 cursor-pointer brand-item">${condition.name}</label>
                            </div>
                         </li>`;
    });

    document.getElementById("condition-section-main").innerHTML = conditionList;
};

const toggleBrands = (categoryId) => {
    const brandList = document.getElementById(`brands-${categoryId}`);
    const icon = document.getElementById(`category-toggle-icon-${categoryId}`);

    if (brandList.style.display === "none") {
        brandList.style.display = "block";
        icon.textContent = "-";
    } else {
        brandList.style.display = "none";
        icon.textContent = "+";
    }
};

const toggleColor = () => {
    const colorList = document.getElementById(`color-section-main`);
    const icon = document.getElementById(`color-toggle-icon`);

    if (colorList.style.display === "none") {
        colorList.style.display = "block";
        icon.textContent = "-";
    } else {
        colorList.style.display = "none";
        icon.textContent = "+";
    }
};

const clearSearch = () => {
    const radioButtons = document.querySelectorAll('input[type="radio"]');

    for (let i = 0; i < radioButtons.length; i++) {
        radioButtons[i].checked = false;
    }

    document.getElementById("search-input").value = "";

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
};