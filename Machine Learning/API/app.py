from flask import Flask,request,jsonify
import numpy as np
import pickle
model = pickle.load(open('model.pkl','rb'))
app = Flask(__name__)
@app.route('/')
def index():
    return "Hello world"
@app.route('/predict',methods=['POST'])
def predict():
    spo2 = request.json.get('spo2')
    bpm = request.json.get('bpm')
    input_query = np.array([[spo2,bpm]])
    result = model.predict(input_query)[0]
    return jsonify({'placement':str(result)})
