const loadProduct = async() => {
    try {
        const response = await fetch("LoadProducts");

        if (response.ok) {

        } else {
            console.error("Fetch failed:", e);
        }
    } catch (e) {
        console.error("Fetch failed:", e);
    }
};