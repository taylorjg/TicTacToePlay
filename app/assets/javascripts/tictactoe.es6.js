const PLAYER1_TURN_MESSAGE = 'Your turn. Click an empty square to make your move.';
const PLAYER2_TURN_MESSAGE = 'The computer is thinking...';
const ARTIFICIAL_THINKING_TIME = 0;
const CROSS = 'X';
const NOUGHT = 'O';
const EMPTY = '-';

const player1Piece = CROSS;
const player2Piece = NOUGHT;

const STATE_NOT_STARTED = 0;
const STATE_HUMAN_MOVE = 1;
const STATE_COMPUTER_MOVE = 2;
const STATE_GAME_OVER = 3;

const HIGHLIGHT_PLAYER1_WIN = 'highlightPlayer1Win';
const HIGHLIGHT_PLAYER2_WIN = 'highlightPlayer2Win';
const HIGHLIGHT_DRAW = 'highlightDraw';
const ALL_HIGHLIGHTS = `${HIGHLIGHT_PLAYER1_WIN} ${HIGHLIGHT_PLAYER2_WIN} ${HIGHLIGHT_DRAW}`;

let state = STATE_NOT_STARTED;
let $cellElements;
let $instructionPanel;
let $instructionMessage;
let $spinner;
let $errorPanel;
let $errorMessage;
let $startButton;

$(document).ready(() => {
    $cellElements = $('#board td').click(makeHumanMove);
    $instructionPanel = $('#instructionPanel');
    $instructionMessage = $('#instructionMessage');
    $spinner = $('#spinner');
    $errorPanel = $('#errorPanel');
    $errorMessage = $('#errorMessage');
    $startButton = $('#startButton').click(start);
    reset();
});

function reset() {
    clearBoard();
    clearInstructionMessage();
    clearErrorMessage();
}

function start() {

    function whoGoesFirst() {
        return (Math.random() < 0.5) ? 1 : 2;
    }

    reset();
    hideStartButton();

    if (whoGoesFirst() === 1) {
        setStateHumanMove();
    }
    else {
        makeComputerMove();
    }
}

function setStateHumanMove() {
    state = STATE_HUMAN_MOVE;
    setInstructionMessage(PLAYER1_TURN_MESSAGE);
}

function setStateComputerMove() {
    state = STATE_COMPUTER_MOVE;
    setInstructionMessageWithSpinner(PLAYER2_TURN_MESSAGE);
    clearErrorMessage();
}

function setStateGameOver() {
    state = STATE_GAME_OVER;
    clearInstructionMessage();
    showStartButton();
}

function makeHumanMove() {
    if (state === STATE_COMPUTER_MOVE) {
        return;
    }
    if (state === STATE_NOT_STARTED || state === STATE_GAME_OVER) {
        start();
        makeHumanMove(...arguments);
    }
    const $cellElement = $(this);
    if (getCell($cellElement) !== EMPTY) {
        return;
    }
    setCell($cellElement, player1Piece);
    makeComputerMove();
}

function makeComputerMove() {

    setStateComputerMove();

    setTimeout(() => {

        const requestData = {
            board: saveBoardToString(),
            player1Piece: player1Piece,
            player2Piece: player2Piece
        };

        $.post({
            url: '/api/computerMove',
            data: JSON.stringify(requestData),
            contentType: 'application/json'
        })
        .always(() => {
            state = STATE_HUMAN_MOVE;
            setInstructionMessage(PLAYER1_TURN_MESSAGE);
        })
        .then(handleComputerMoveResponse)
        .catch(handleComputerMoveError);
    }, ARTIFICIAL_THINKING_TIME);
}

function handleComputerMoveResponse(state) {
    updateBoardFromString(state.board);
    if (state.outcome) {
        switch (state.outcome) {
            case 1:
                highlightCells(state.winningLine, HIGHLIGHT_PLAYER1_WIN);
                break;
            case 2:
                highlightCells(state.winningLine, HIGHLIGHT_PLAYER2_WIN);
                break;
            case 3:
                highlightCells([0,1,2,3,4,5,6,7,8], HIGHLIGHT_DRAW);
                break;
        }
        setStateGameOver();
    }
}

function handleComputerMoveError(xhr) {
    const statusText = xhr.statusText;
    const statusCode = xhr.status ? `(${xhr.status})` : '';
    if (statusText && statusText !== 'error') {
        setErrorMessage(`Error during computer move: ${statusText} ${statusCode}`);
    }
    else {
        setErrorMessage(`Error during computer move`);
    }
}

function getCell($cellElement) {
    var piece = $cellElement.html();
    return piece === CROSS || piece === NOUGHT ? piece : EMPTY;
}

function setCell($cellElement, piece) {
    $cellElement.html(piece === CROSS || piece === NOUGHT ? piece : '&nbsp;');
}

function highlightCells(cellIndices, cssClass) {
    $cellElements
        .filter(cellIndex => cellIndices.includes(cellIndex))
        .addClass(cssClass);
}

function saveBoardToString() {
    return $cellElements.map((cellIndex, cellElement) => getCell($(cellElement))).get().join('');
}

function clearBoard() {
    updateBoardFromString(EMPTY.repeat(9));
    $cellElements.removeClass(ALL_HIGHLIGHTS);
}

function updateBoardFromString(board) {
    $cellElements.each((cellIndex, cellElement) => {
        setCell($(cellElement), board.charAt(cellIndex));
    });
}

function setInstructionMessage(message) {
    $instructionMessage.html(message);
    $instructionPanel.show();
    hideSpinner();
}

function setInstructionMessageWithSpinner(message) {
    $instructionMessage.html(message);
    $instructionPanel.show();
    showSpinner();
}

function clearInstructionMessage() {
    $instructionPanel.hide();
}

function setErrorMessage(errorMessage) {
    $errorMessage.html(errorMessage);
    $errorPanel.show();
}

function clearErrorMessage() {
    $errorPanel.hide();
}

function showStartButton() {
    $startButton.show();
}

function hideStartButton() {
    $startButton.hide();
}

function showSpinner() {
    $spinner.show();
}

function hideSpinner() {
    $spinner.hide();
}
