FROM python

WORKDIR /usr/src/tg

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

WORKDIR /usr/src/tg
COPY . .

CMD ["bash", "-c", "python main.py"]