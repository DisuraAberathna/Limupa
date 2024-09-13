const loadData = async() => {
    try {
        const response = await fetch("LoadSearchData");

        if (response.ok) {
            const data = await response.json();

            let categoryHtml = document.getElementById("category-section");
            document.getElementById("category-section-main").innerHTML = "";

            data.categoryList.forEach(category => {
                let categoryCloneHtml = categoryHtml.cloneNode(true);

                categoryCloneHtml.querySelector("#category-title").innerHTML = category.name;
                document.getElementById("category-section-main").appendChild(categoryCloneHtml);

            });
        } else {
            console.error("Fetch failed:", e);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};