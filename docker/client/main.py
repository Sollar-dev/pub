import logging
from telegram import Update
from telegram.ext import ApplicationBuilder, ContextTypes, CommandHandler
import sqlite3

logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)

tt = ''
 
async def callback_timer(update: Update, context: ContextTypes.DEFAULT_TYPE):
    chat_id = update.effective_chat.id
    await context.bot.send_message(chat_id=chat_id, text=tt)
    name = update.effective_chat.full_name
    context.job_queue.run_repeating(start, interval=3, data=name, chat_id=chat_id) # first = 3, send "SettSetting a timer" after 3 sec

async def start(context: ContextTypes.DEFAULT_TYPE):
    await context.bot.send_message(chat_id=context.job.chat_id, text=f'hello world {context.job.data}!')

if __name__ == '__main__':
    connection = sqlite3.connect('db/test.db')
    cursor = connection.cursor()
    cursor.execute('''SELECT * FROM states''')
    tts = cursor.fetchall()
    tt = tts[0]
    connection.close()

    application = ApplicationBuilder().token('token').build()
    
    timer_handler = CommandHandler('timer', callback_timer)
    
    application.add_handler(timer_handler)
    
    application.run_polling()