const chatController = require('../Controllers/chat.controller');
const userController = require('../Controllers/user.controller');

module.exports = function (io) {
  io.on('connection', async (socket) => {
    console.log('client is connected', socket.id);

    socket.on('login', async (userName, cb) => {
      // console.log('backend', userName);
      // 이름 받아옴 12/24 11:03
      try {
        // 유저 정보 저장
        const user = await userController.saveUser(userName, socket.id);

        // 환영 메세지
        const welcomeMessage = {
          chat: `'${user.name}' 님이 참가했습니다. 대화를 걸어보세요!`,
          user: { id: null, name: 'system' },
        };
        io.emit('message', welcomeMessage);

        // 잘 받아왔다고 콜백함수 //괜찮아? 그럼 데이터로 유저정보 보낼게
        cb({ ok: true, data: user });
      } catch (error) {
        cb({ ok: false, error: error.message });
      }
    });

    socket.on('sendMessage', async (message, cb) => {
      try {
        // 유저찾기 - socket id로 찾기
        const user = await userController.checkUser(socket.id);
        // 메세지 저장
        const newMessage = await chatController.saveChat(message, user);
        io.emit('message', newMessage);
        cb({ ok: true });
      } catch (error) {
        cb({ ok: false, error: error.message });
      }
    });

    socket.on('disconnect', () => {
      console.log('user is disconnected');
    });
    socket.on('disconnect', async () => {
      try {
        // 유저찾기 - socket id로 찾기
        const user = await userController.checkUser(socket.id);
        if (user) {
          const goodbyeMessage = {
            chat: `'${user.name}' 님이 나갔습니다.`,
            user: { id: null, name: 'system' },
          };
          io.emit('message', goodbyeMessage);
        }
      } catch (error) {
        console.error('Error handling disconnect event:', error);
      }
    });
  });
};
