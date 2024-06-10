package com.example.reactiongame;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

// Controlador da tela principal do jogo
public class GameInterfaceController {

    // Botao do player um
    @FXML
    private Button buttonPlayerOne;

    // Botao do player dois
    @FXML
    private Button buttonPlayerTwo;

    // Botao start
    @FXML
    private Button buttonStart;

    // Campo de texto para escrever o vencedor e contagem regressiva
    @FXML
    private Label labelField;

    // Campos de Placar que indicam o vencedor de cada partida
    @FXML
    private Label labelPlacarOne;
    @FXML
    private Label labelPlacarTwo;
    @FXML
    private Label labelPlacarThree;


    // Label que representa o "LED" de cada player que apertar o botao primeiro
    @FXML
    private Label labelColorOne;
    @FXML
    private Label labelColorTwo;

    // Vetor que armazena as vitorias de cada jogador (1 - player um e 2 - player dois)
    int[] vitorias = new int[3];

    // Armazena o numero da partida atual de 0 ate 3
    int placar = 0;

    // Contador para a contagem regressiva de 3 ate 0
    int contador = 0;

    // Contadores para cada jogador para liberar a execucao da thread ou manter no modo de espera pelo jogador
    private volatile CountDownLatch playerOneLatch;
    private volatile CountDownLatch playerTwoLatch;

    // Thread de cada jogador
    Thread threadPlayerOne;
    Thread threadPlayerTwo;

    // Procedimento que avalia o jogador vencedor do jogo
    public void escreverVencedor(int i) {
        
        // Verifica qual o jogador vecendor da partida
        if (i == 1) {
            labelField.setText("Jogador 1 Ganhou!");
            labelField.setStyle("-fx-border-color: blue; -fx-border-width: 3px;");
        } else if (i == 2) {
            labelField.setText("Jogador 2 Ganhou!");
            labelField.setStyle("-fx-border-color: tomato; -fx-border-width: 3px;");
        }

        // Cria uma pausa de 2 segundos
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            // Reinicia contagem para proxima partida
            if (threadPlayerOne.isAlive() && threadPlayerTwo.isAlive())  {
                threadPlayerOne.stop();
                threadPlayerTwo.stop();
                threadPlayerOne.interrupt();
                threadPlayerTwo.interrupt();
            }
            actionStart();
        });
        pause.play();
    }

    // Procedimento que avalia o jogador vencedor da partida
    public void avaliarVencedor() {
        
        int A = 0, B = 0;
        for (int valor: vitorias) {
            if (valor == 1) {
                A += 1;
            } else {
                B += 1;
            }
        }
        if (A > B) {
            labelField.setText("Jogador 1 Campeão!");
            labelField.setStyle("-fx-border-color: blue; -fx-border-width: 3px;");
            labelColorOne.setStyle("-fx-background-color: blue; -fx-border-color: white; -fx-border-width: 3px;");

        } else {
            labelField.setText("Jogador 2 Campeão!");
            labelField.setStyle("-fx-border-color: tomato; -fx-border-width: 3px;");
            labelColorTwo.setStyle("-fx-background-color: tomato; -fx-border-color: white; -fx-border-width: 3px;");

        }

        if (threadPlayerOne.isAlive() && threadPlayerTwo.isAlive())  {
            threadPlayerOne.stop();
            threadPlayerTwo.stop();
            threadPlayerOne.interrupt();
            threadPlayerTwo.interrupt();
        }

        Arrays.fill(vitorias, 0);
        placar = 0;

        buttonStart.setDisable(false);

    }

    // Procedimento que muda a cor do LED do jogador 1
    public void actionColorOne() {
        labelColorOne.setStyle("-fx-background-color: blue; -fx-border-color: white; -fx-border-width: 3px;");
    }

    // Procedimento que muda a cor do LED do jogador 2
    public void actionColorTwo() {
        labelColorTwo.setStyle("-fx-background-color: tomato; -fx-border-color: white; -fx-border-width: 3px;");
    }

    // Procedimento que da a vitoria dos jogadores no label
    public void escreverVitoria (int i) {
        
        // Verifica qual jogador ganhou a partida
        if (i == 1) {
            vitorias[placar] = i;
        } else if (i == 2) {
            vitorias[placar] = i;
        }

        // Verifica o qual o placar atual e atualiza o label do placar do vencedor da partida atual
        if (placar == 0) {
            // Escreve o numero do jogador vencedor
            labelPlacarOne.setText(Integer.toString(i));
            // Estiliza o label conforme o vencedor
            if (i == 1) {
                labelPlacarOne.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
            } else {
                labelPlacarOne.setStyle("-fx-border-color: tomato; -fx-border-width: 2px;");
            }
        } else if (placar == 1) {
            labelPlacarTwo.setText(Integer.toString(i));
            if (i == 1) {
                labelPlacarTwo.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
            } else {
                labelPlacarTwo.setStyle("-fx-border-color: tomato; -fx-border-width: 2px;");
            }
        } else if (placar == 2) {
            labelPlacarThree.setText(Integer.toString(i));
            if (i == 1) {
                labelPlacarThree.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
            } else {
                labelPlacarThree.setStyle("-fx-border-color: tomato; -fx-border-width: 2px;");
            }
        }

        placar += 1;
    }

    // Procedimeto do botao do player um, libera a thread
    public void actionPlayerOne() {
        playerOneLatch.countDown();
    }

    // Procedimeto do botao do player dois, libera a thread
    public void actionPlayerTwo() {
        playerTwoLatch.countDown();
    }

    // Procedimento que inicializa as threads
    public void initializePlayers() {
        PlayerOneTask playeronetask = new PlayerOneTask();
        PlayerTwoTask playertwotask = new PlayerTwoTask();

        threadPlayerOne = new Thread(playeronetask);
        threadPlayerTwo = new Thread(playertwotask);

        playerOneLatch = new CountDownLatch(1);
        playerTwoLatch = new CountDownLatch(1);

        threadPlayerOne.start();
        threadPlayerTwo.start();
    }

    // Classe em execucao na thread do jogador um
    class PlayerOneTask implements Runnable {

        @Override
        public void run() {
            //synchronized (this) {
                try {
                    Platform.runLater(() -> {
                        // Define o foco do teclado para o botão buttonPlayerOne
                        buttonPlayerOne.requestFocus();

                        // Desabilita ação quando botão clicado pelo mouse
                        buttonPlayerOne.setOnAction(null);

                        // Adiciona um ouvinte de evento de teclado ao botão
                        buttonPlayerOne.setOnKeyPressed(event2 -> {
                            if (event2.getCode() == KeyCode.ENTER) {
                                // Ao teclar ENTER libera a thread
                                event2.consume();
                                actionPlayerOne();
                            }
                        });
                    });

//                    Thread.sleep(500);
//                    buttonPlayerOne.fire();
//                    actionPlayerOne();

                    // Fica esperando ate que o contador seja decrementado
                    playerOneLatch.await();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Agende a lógica para o jogador um após o clique
                Platform.runLater(() -> {
                    // Desabilita os botoes dos jogadores
                    buttonPlayerOne.setDisable(true);
                    buttonPlayerTwo.setDisable(true);

                    escreverVitoria(1);
                    actionColorOne();
                    if (placar == 3) {
                        avaliarVencedor();
                    } else {
                        escreverVencedor(1);
                    }
                });
            //}
        }
    }

    // Classe em execucao na thread do jogador dois
    class PlayerTwoTask implements Runnable {
        @Override
        public void run() {
            synchronized (this) {
                try {
                    Platform.runLater(() -> {
                        // Desabilita a ação do botão do jogador dois para eventos de teclado
                        buttonPlayerTwo.setOnKeyPressed(event -> event.consume());

                        // Limpa o buffer do evento do mouse (desabilita a ação do botão para cliques do mouse)
                        buttonPlayerTwo.setOnMouseClicked(event -> {
                            event.consume();
                            // Adicione qualquer ação adicional que desejar
                            actionPlayerTwo();
                        });
                    });

//                    Thread.sleep(500);
//                    buttonPlayerTwo.fire();
//                    actionPlayerTwo();

                    // Espera enquanto o contador nao for decrementado
                    playerTwoLatch.await();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Agende a lógica para o jogador dois após o clique
                Platform.runLater(() -> {
                    // Desabilita os botoes
                    buttonPlayerOne.setDisable(true);
                    buttonPlayerTwo.setDisable(true);

                    escreverVitoria(2);
                    actionColorTwo();
                    if (placar == 3) {
                        avaliarVencedor();
                    } else {
                        escreverVencedor(2);
                    }
                });
            }
        }
    }

    // Procedimento para resetar todos os campos do jogo
    public void restartFields() {
        // Reseta o placar para o novo jogo
        if (placar == 0) {
            labelPlacarOne.setText("Placar 1");
            labelPlacarOne.setStyle("-fx-border-color: white; -fx-border-width: 2px;");

            labelPlacarTwo.setText("Placar 2");
            labelPlacarTwo.setStyle("-fx-border-color: white; -fx-border-width: 2px;");

            labelPlacarThree.setText("Placar 3");
            labelPlacarThree.setStyle("-fx-border-color: white; -fx-border-width: 2px;");

        }

        // Reseta os labels
        labelField.setStyle("-fx-border-color: white; -fx-border-width: 3px;");
        labelColorOne.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 3px;");
        labelColorTwo.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 3px;");

        // Reseta o contador de contagem regressiva para iniciar o jogo
        if (contador == 0) {
            contador = 3;
        }

        // Desabilita todos os botoes
        buttonPlayerOne.setDisable(true);
        buttonPlayerTwo.setDisable(true);
        buttonStart.setDisable(true);
    }

    // Procedimento para simular a contagem regressiva de inicio de jogo
    public void contagemRegressiva() {
        // Exibe no Label o contador definido em 3
        labelField.setText(Integer.toString(contador));

        // Cria uma Timeline para a animação
        Timeline timeline = new Timeline();

        // Inicia um loop para criar os KeyFrames da contagem regressiva
        for (int i = contador; i >= 0; i--) {
            final int valor = i; // Salva o valor atual de i em uma variável final

            // Cria um KeyFrame para atualizar o Label
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(contador - valor), // Define a duração do KeyFrame
                    e -> {
                        // Atualiza o Label com o valor atual
                        labelField.setText(Integer.toString(valor));
                    }
            );

            // Adiciona o KeyFrame à Timeline
            timeline.getKeyFrames().add(keyFrame);
        }

        // Define a ação a ser executada quando a Timeline terminar
        timeline.setOnFinished(e -> {
            // Da a partida no jogo
            labelField.setText("VALENDO!");
            // Reabilita os botões "Player One" e "Player Two"
            buttonPlayerOne.setDisable(false);
            buttonPlayerTwo.setDisable(false);
        });

        // Define o número de ciclos da Timeline como 1 (não repetirá)
        timeline.setCycleCount(1);

        // Inicia a animação da Timeline
        timeline.play();
    }

    // Procedimeto para o botao start do jogo
    public void actionStart() {
        // Reseta os campos
        restartFields();
        // Faz a contagem regressiva
        contagemRegressiva();
        // Inicializa as threads
        initializePlayers();
    }
}