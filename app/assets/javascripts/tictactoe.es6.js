const START_MESSAGE = 'Click the Start button to start a new game.';
const PLAYER1_TURN_MESSAGE = 'Your turn. Click an empty square to make your move.';
const PLAYER2_TURN_MESSAGE = 'The computer is thinking...';
const PLAYER1_WON_MESSAGE = 'You won!';
const PLAYER2_WON_MESSAGE = 'The computer won!';
const DRAW_MESSAGE = 'It\'s a draw!';
const ARTIFICIAL_THINKING_TIME = 500;
const CROSS = 'X';
const NOUGHT = 'O';
const EMPTY = '-';

// TODO: Allow the human player to choose whether to be NOUGHTS or CROSSES ?
const player1Piece = CROSS;
const player2Piece = NOUGHT;

const STATE_NOT_STARTED = 0;
const STATE_HUMAN_MOVE = 1;
const STATE_COMPUTER_MOVE = 2;
const STATE_GAME_OVER = 3;

let state = STATE_NOT_STARTED;
let cellElements;

$(document).ready(() => {
    $('#startBtn').click(start);
    const cellIds = [
        '#cell00', '#cell01', '#cell02',
        '#cell10', '#cell11', '#cell12',
        '#cell20', '#cell21', '#cell22'
    ];
    cellElements = cellIds.map(id => $(id));
    cellElements.forEach(ce => ce.click(onCellClick));
    reset();
    setMessages(START_MESSAGE);
});

function reset() {
    clearBoard();
    hideSpinner();
}

function start() {
    reset();
    state = STATE_HUMAN_MOVE;
    setMessages(PLAYER1_TURN_MESSAGE);
    hideStartButton();
}

function gameOver() {
    state = STATE_GAME_OVER;
    showStartButton();
}

function onCellClick(/* e */) {
    if (state !== STATE_HUMAN_MOVE) {
        return;
    }
    const cellElement = $(this);
    if (getCell(cellElement) !== EMPTY) {
        return;
    }
    setCell(cellElement, player1Piece);
    makeComputerMove();
}

function makeComputerMove() {

    state = STATE_COMPUTER_MOVE;
    setMessagesWithSpinner(PLAYER2_TURN_MESSAGE);

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
            setMessages(PLAYER1_TURN_MESSAGE);
        })
        .then(handleComputerMoveResponse)
        .catch(handleComputerMoveError);
    }, ARTIFICIAL_THINKING_TIME);
}

function handleComputerMoveResponse(state) {
    updateBoardFromString(state.board);
    if (state.outcome) {
        let message1;
        switch (state.outcome) {
            case 1:
                highlightWinningLine(state.winningLine);
                message1 = PLAYER1_WON_MESSAGE;
                break;
            case 2:
                highlightWinningLine(state.winningLine);
                message1 = PLAYER2_WON_MESSAGE;
                break;
            case 3:
                message1 = DRAW_MESSAGE;
                break;
        }
        gameOver();
        setMessages(message1, START_MESSAGE);
    }
}

function handleComputerMoveError(xhr, statusText, error) {
    console.log(xhr, statusText, error);
}

function getCell(cellElement) {
    var piece = cellElement.html();
    return piece === CROSS || piece === NOUGHT ? piece : EMPTY;
}

function setCell(cellElement, piece) {
    cellElement.html(piece === CROSS || piece === NOUGHT ? piece : '');
}

function highlightWinningLine(cellIndices) {
    cellIndices.forEach(cellIndex => {
        cellElements[cellIndex].addClass('highlight');
    });
}

function saveBoardToString() {
    return cellElements.reduce((acc, ce) => {
        acc += getCell(ce);
        return acc;
    }, '');
}

function clearBoard() {
    updateBoardFromString(EMPTY.repeat(9));
    cellElements.forEach(ce => ce.removeClass('highlight'));
}

function updateBoardFromString(board) {
    cellElements.forEach((ce, index) => {
        setCell(ce, board.charAt(index));
    });
}

function setMessages(...messages) {
    $('#messageArea').html(messages.join('<br />'));
    hideSpinner();
}

function setMessagesWithSpinner(...messages) {
    $('#messageArea').html(messages.join('<br />'));
    showSpinner();
}

function showStartButton() {
    $('#startBtn').show();
}

function hideStartButton() {
    $('#startBtn').hide();
}

function showSpinner() {
    $('#spinner').show();
}

function hideSpinner() {
    $('#spinner').hide();
}
