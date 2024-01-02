const { createServer } = require('http');
const app = require('./app');
const { Server } = require('socket.io');
require('dotenv').config();

const httpServer = createServer(app);
const io = new Server(httpServer, {
  //io의 비활성화 - utils 폴더에 io.js 파일 생성해서 관리
  cors: {
    origin: 'http://localhost:3000',
  },
});

require('./utils/io')(io);
httpServer.listen(process.env.PORT, () => {
  console.log('server listening on port', process.env.PORT);
});
