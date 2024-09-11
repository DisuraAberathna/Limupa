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

            console.log(data.productList);

            loadSelectOptions("category-select", categoryList, ["id", "name", "status"]);
            loadSelectOptions("brand-select", brandList, ["id", "name", "status"]);
            loadSelectOptions("model-select", modelList, ["id", "name", "status"]);
            loadSelectOptions("color-select", colorList, ["id", "name"]);
            loadSelectOptions("condition-select", conditionList, ["id", "name"]);
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
