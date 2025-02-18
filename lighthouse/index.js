/*
    Automating WPP using Lighthouse

    Installation:
    npm install lighthouse chrome-launcher
*/

const lighthouse = require('lighthouse');
const chromeLauncher = require('chrome-launcher');

function getPageInsights(url, flags = {}, config = null) {
    return chromeLauncher.launch(flags).then(chrome => {
        flags.port = chrome.port;
        return lighthouse(url, flags, config).then(results =>
            chrome.kill().then(() => results.report));
    });
}

(async () => {

    var tti = 0;
    var fcp = 0;
    var fmp = 0;

    var url = "https://educative.io";
    const flags = {
        chromeFlags: ['--window-size=1240,960', '--disable-device-emulation', '--headless','--no-sandbox']
    };

    await getPageInsights(url, flags).then(results => {
        var json = JSON.parse(results);
        tti = json["audits"]["interactive"]["numericValue"];
        fcp = json["audits"]["first-contentful-paint"]["numericValue"];
        fmp = json["audits"]["first-meaningful-paint"]["numericValue"];
    });

    console.log(`(FCP) first contentful paint: ${fcp.toFixed(2)} ms`);
    console.log(`(FMP) first meaningful paint: ${fmp.toFixed(2)} ms`);
    console.log(`(TTI) time to interact: ${tti.toFixed(2)} ms`);

})();
