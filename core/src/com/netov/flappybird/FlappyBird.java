package com.netov.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaro;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private BitmapFont fonte;
	private BitmapFont menssagem;

	private float variacao = 0;
	private float velocidadeQueda;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private  float deltaTime;
	private float alturaEntreCanosRandomica;
	private int estadoJogo = 0;// 0 = jogo nao iniciado, 1 = iniciado = 2 = game over
	private boolean marcouPonto;
	private int pontuacao = 0;
	private int posicaoPassaroHorizontal;
	private Random numeroRandomico;

	private float larguraDispositivo;
	private float aluturaDispositivo;

	private Circle passaroCircle;
	private Rectangle rectangleCanoTopo;
	private Rectangle rectangleCanoBaixo;
	//private ShapeRenderer shape;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;
	@Override
	public void create () {
		batch = new SpriteBatch();
		numeroRandomico = new Random();
		fonte = new BitmapFont();
		menssagem = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().scale(7);

		menssagem.setColor(Color.WHITE);
		menssagem.getData().scale(2);

		passaroCircle = new Circle();
		/*rectangleCanoBaixo = new Rectangle();
		rectangleCanoTopo = new Rectangle();
		shape = new ShapeRenderer();*/

		passaro = new Texture[3];
		passaro[0] = new Texture("Bird1.png");
		passaro[1] = new Texture("Bird2.png");
		passaro[2] = new Texture("Bird3.png");

		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");

		fundo = new Texture("fundo.png");
		gameOver = new Texture("game_over.png");

		aluturaDispositivo = VIRTUAL_HEIGHT;
		larguraDispositivo = VIRTUAL_WIDTH;
		posicaoInicialVertical = Gdx.graphics.getBackBufferHeight() / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo - 100;
        espacoEntreCanos = 250;
        deltaTime = Gdx.graphics.getDeltaTime();
        posicaoPassaroHorizontal = 120;

        //configurações da camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2,VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

	}

	@Override
	public void render () {
	    camera.update();
	    //limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 7;
        if (variacao > 2) variacao = 0;

	    if (estadoJogo == 0) {
            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }
        } else {

            velocidadeQueda += 0.5;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical -= velocidadeQueda;

            if(estadoJogo == 1) {
                posicaoMovimentoCanoHorizontal -= deltaTime * 500;

                if (Gdx.input.justTouched()) {
                    velocidadeQueda = -15;
                }

                //testar se o cano ja saiu da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                if (posicaoMovimentoCanoHorizontal < posicaoPassaroHorizontal) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            }else{// tela game over
                if(Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    posicaoInicialVertical = aluturaDispositivo / 2;
                }
            }
        }
            //configurações da camera
            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            batch.draw(fundo, 0, 0, larguraDispositivo, aluturaDispositivo);
            batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, aluturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
            batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, aluturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 +
                    alturaEntreCanosRandomica);
            batch.draw(passaro[(int) variacao], posicaoPassaroHorizontal, posicaoInicialVertical);

            fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, aluturaDispositivo - 25);
            if(estadoJogo == 2){
                batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, aluturaDispositivo / 2);
                menssagem.draw(batch, "Toque para reiniciar!",larguraDispositivo / 2 - 200, aluturaDispositivo / 2 - gameOver.getHeight() / 2 );
            }
            batch.end();

            passaroCircle.set(posicaoPassaroHorizontal + passaro[0].getWidth() / 2, posicaoInicialVertical +
                    passaro[1].getHeight() / 2, passaro[1].getWidth() / 2);

            rectangleCanoBaixo = new Rectangle(posicaoMovimentoCanoHorizontal, aluturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 +
                    alturaEntreCanosRandomica, canoBaixo.getWidth(), canoBaixo.getHeight());

            rectangleCanoTopo = new Rectangle(posicaoMovimentoCanoHorizontal, aluturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoTopo.getWidth(),
                    canoTopo.getHeight());

            /*shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.circle(passaroCircle.x, passaroCircle.y, passaroCircle.radius);
            shape.setColor(Color.RED);
            shape.rect(rectangleCanoBaixo.x, rectangleCanoBaixo.y, rectangleCanoBaixo.width, rectangleCanoBaixo.height);
            shape.rect(rectangleCanoTopo.x, rectangleCanoTopo.y, rectangleCanoTopo.width, rectangleCanoTopo.height);
            shape.end();*/

            //teste de colisões
            if(Intersector.overlaps(passaroCircle, rectangleCanoBaixo)|| Intersector.overlaps(passaroCircle, rectangleCanoTopo)||
                    posicaoInicialVertical <= 0 || posicaoInicialVertical >= aluturaDispositivo){
                estadoJogo = 2;
            }
        }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
