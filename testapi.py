import logging
import traceback
from flask import Flask, request, jsonify
from openai import OpenAI
from flask_cors import CORS  # 引入 CORS

# 设置日志记录
logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# 创建 Flask 应用并启用 CORS
app = Flask(__name__)
CORS(app)  # 启用 CORS

# OpenAI 客户端配置
client = OpenAI(api_key="sk-QjwicNbZGl3vItLH9164DaE7Ed46434a97Da8aB00bCcBd99", base_url="https://vip.apiyi.com/v1")

@app.route('/generate-text', methods=['POST'])
def generate_text():
    try:
        # 获取请求的 JSON 数据
        data = request.json
        user_message = data.get('userMessage')
        style = data.get('style', '')
        length = data.get('length', '')

        # 记录收到的请求数据
        logger.debug(f"Received request with user_message: {user_message}, style: {style}, length: {length}")

        if not user_message:
            logger.error("No user_message provided")
            return jsonify({'error': 'User message is required'}), 400

        # 调用 OpenAI API
        logger.debug("Calling OpenAI API...")
        completion = client.chat.completions.create(
            model="gpt-3.5-turbo",
            stream=False,
            messages=[
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": user_message}
            ]
        )

        # 获取生成的文本并返回给前端
        generated_text = completion.choices[0].message.content

        logger.debug(f"Generated text: {generated_text}")

        return jsonify({'generatedText': generated_text})

    except Exception as e:
        # 捕获并记录详细的错误信息
        logger.error(f"Error occurred: {str(e)}")
        logger.error("Traceback: ", exc_info=True)  # 打印详细的堆栈跟踪
        return jsonify({'error': 'Internal server error'}), 500

if __name__ == '__main__':
    # 启动 Flask 应用
    app.run(debug=True)
