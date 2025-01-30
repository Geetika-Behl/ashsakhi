//class VoiceRecognitionHelper(private val context: Context, private val onCommandDetected: (String) -> Unit) {
//
//    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
//
//    init {
//        speechRecognizer.setRecognitionListener(object : RecognitionListener {
//            override fun onReadyForSpeech(params: Bundle?) {
//                Log.d("VoiceRecognition", "Ready for speech")
//            }
//
//            override fun onBeginningOfSpeech() {
//                Log.d("VoiceRecognition", "Speech started")
//            }
//
//            override fun onRmsChanged(rmsdB: Float) {
//                // Handle RMS changes (optional)
//            }
//
//            override fun onBufferReceived(buffer: ByteArray?) {
//                // Handle buffer received (optional)
//            }
//
//            override fun onEndOfSpeech() {
//                Log.d("VoiceRecognition", "Speech ended")
//                startListening() // Restart listening
//            }
//
//            override fun onError(error: Int) {
//                Log.e("VoiceRecognition", "Error: $error")
//            }
//
//            override fun onResults(results: Bundle?) {
//                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
//                    if (matches.isNotEmpty()) {
//                        val command = matches[0].lowercase(Locale.getDefault())
//                        Log.d("VoiceRecognition", "Detected: $command")
//
//                        if (command.contains("help")) {
//                            onCommandDetected("help")  // Notify MainActivity that "help" was detected
//                        }
//                    }
//                }
//            }
//
//            override fun onPartialResults(partialResults: Bundle?) {
//                // Handle partial results (optional)
//            }
//
//            override fun onEvent(eventType: Int, params: Bundle?) {
//                // Handle events (optional)
//            }
//        })
//    }
//
//    fun startListening() {
//        val recognizerIntent = RecognizerIntent().apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
//        }
//        speechRecognizer.startListening(recognizerIntent)
//    }
//
//    fun stopListening() {
//        speechRecognizer.stopListening()
//    }
//}
