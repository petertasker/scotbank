document.addEventListener("DOMContentLoaded", function () {
    function getTopSpenderData(type) {
        let selector;
        if (type === 'amount') {
            selector = '#top-spenders .spender-item';
        }

        const spenderElements = document.querySelectorAll(selector);
        const data = [];

        spenderElements.forEach(element => {
            const name = element.querySelector('.spender-name').textContent.trim();
            const postcode = element.querySelector('.spender-postcode').textContent.replace('(', '').replace(')', '').trim();
            let totalAmount;

            if (type === 'amount') {
                totalAmount = parseFloat(element.querySelector('.spender-amount').textContent.replace('£', '').replace(',', ''));
            }

            if (!isNaN(totalAmount)) {
                data.push({ name, postcode, totalAmount });
            }
        });

        return data;
    }

    // Call the function and log the result after DOM is fully loaded
    const topSpenders = getTopSpenderData('amount');
    console.log(topSpenders);
    // var map;
    // var geocoder;


    // function initMap() {
    //     map = new google.maps.Map(document.getElementById('map'), {
    //         zoom: 5,
    //         center: { lat: 56.4907, lng: -4.2026 } // Scotland
    //     });
    //
    //     geocoder = new google.maps.Geocoder();
    //     plotSpenders();
    // }
    //
    // function plotSpenders() {
    //     bigSpenders.forEach(spender => {
    //         geocoder.geocode({ address: spender.Postcode }, function (results, status) {
    //             if (status === "OK") {
    //                 new google.maps.Marker({
    //                     position: results[0].geometry.location,
    //                     map: map,
    //                     title: `${spender.Name}: £${spender.TotalAmount}`
    //                 });
    //             } else {
    //                 console.error("Geocode failed for " + spender.Postcode + ": " + status);
    //             }
    //         });
    //     });
    // }
    //
    // window.initMap = initMap; // Expose initMap globally for Google Maps API
});
