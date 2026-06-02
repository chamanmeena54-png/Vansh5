export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    const pathLower = url.pathname.toLowerCase();

    const targetTelegramMain = "https://t.me/+SDQNy0c8-p1iNDBl";
    const sellerBotLink = "https://t.me/seller_connector_bot";

    // 1. STRICT REDIRECTION RULES (Server-Level Interception)
    
    // Check for Exact Match of /study/batches
    if (pathLower === '/study/batches' || pathLower === '/study/batches/') {
      return Response.redirect(sellerBotLink, 302);
    }

    // Check for Contact, Donate or old Telegram references
    if (pathLower === '/contact' || pathLower === '/contact/' || 
        pathLower === '/study/donate' || pathLower === '/study/donate/' ||
        pathLower.includes('t.me/pw_thor') || pathLower.includes('pw_thor1')) {
      return Response.redirect(targetTelegramMain, 302);
    }

    // 2. FOR ALL OTHER PATHS: Securely Mask and Forward to target site
    // Bypasses browser iframe restrictions completely because it runs on server side
    const modifiedRequest = new Request(request, {
      headers: new Headers(request.headers)
    });
    
    // Set masking and security handshakes
    modifiedRequest.headers.set("Host", "pwthor.live");
    modifiedRequest.headers.set("Referer", "https://pwthor.live/");

    try {
      const response = await fetch(`https://pwthor.live${url.pathname}${url.search}`, modifiedRequest);
      
      // Copy original response but keep the execution domain as YOUR custom domain
      return new Response(response.body, response);
    } catch (e) {
      return new Response("Secure Gateway Error. Please try again.", { status: 502 });
    }
  }
};
