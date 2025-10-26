const http = require('http');

const handleRequest = (req, res) => {
    console.log('Received request for ' + req.url);
    res.writeHead(200, {'Content-Type': 'text/plain'});
    res.end('Hello from the Kubernetes practice server!\n');
};

const PORT = 8080;
const HOST = '0.0.0.0';

const server = http.createServer(handleRequest);
server.listen(PORT, HOST, () => {
    console.log(`Server running at http://${HOST}:${PORT}/`);
});
