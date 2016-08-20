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

let state = STATE_NOT_STARTED;
let $cellElements;
let $instructionPanel;
let $instructionMessage;
let $spinner;
let $errorPanel;
let $errorMessage;
let $startButton;

$(document).ready(() => {
    const cellIds = [
        '#cell00', '#cell01', '#cell02',
        '#cell10', '#cell11', '#cell12',
        '#cell20', '#cell21', '#cell22'
    ];
    $cellElements = $(cellIds.join(','));
    $instructionPanel = $('#instructionPanel').click(start);
    $instructionMessage = $('#instructionMessage').click(start);
    $spinner = $('#spinner').click(start);
    $errorPanel = $('#errorPanel').click(start);
    $errorMessage = $('#errorMessage').click(start);
    $startButton = $('#startButton').click(start);
    $cellElements.click(onCellClick);
    reset();
});

function reset() {
    clearBoard();
    clearInstructionMessage();
    clearErrorMessage();
}

function start() {
    reset();
    state = STATE_HUMAN_MOVE;
    setInstructionMessage(PLAYER1_TURN_MESSAGE);
    hideStartButton();
}

function gameOver() {
    state = STATE_GAME_OVER;
    clearInstructionMessage();
    showStartButton();
}

function onCellClick() {
    if (state === STATE_COMPUTER_MOVE) {
        return;
    }
    if (state === STATE_NOT_STARTED || state === STATE_GAME_OVER) {
        start();
    }
    const $cellElement = $(this);
    if (getCell($cellElement) !== EMPTY) {
        return;
    }
    setCell($cellElement, player1Piece);
    makeComputerMove();
}

function makeComputerMove() {

    state = STATE_COMPUTER_MOVE;
    setInstructionMessageWithSpinner(PLAYER2_TURN_MESSAGE);
    clearErrorMessage();

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
                highlightWinningLine(state.winningLine, 'highlightPlayer1Win');
                break;
            case 2:
                highlightWinningLine(state.winningLine, 'highlightPlayer2Win');
                break;
        }
        gameOver();
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
    $cellElement.html(piece === CROSS || piece === NOUGHT ? piece : '');
}

function highlightWinningLine(cellIndices, cssClass) {
    cellIndices.forEach(cellIndex => {
        $cellElements.eq(cellIndex).addClass(cssClass);
    });
}

function saveBoardToString() {
    return $cellElements.toArray().reduce((acc, cellElement) => {
        return acc += getCell($(cellElement));
    }, '');
}

function clearBoard() {
    updateBoardFromString(EMPTY.repeat(9));
    $cellElements
        .removeClass('highlightPlayer1Win')
        .removeClass('highlightPlayer2Win');
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
