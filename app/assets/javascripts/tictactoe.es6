const CHOOSE_PIECE_MESSAGE = 'Choose noughts or crosses then click the Start button.';
const START_MESSAGE = 'Click the Start button to start a new game.';
const PLAYER1_TURN_MESSAGE = 'Your turn. Click an empty square to make your move.';
const PLAYER2_TURN_MESSAGE = 'The computer is thinking...';
const PLAYER1_WON_MESSAGE = 'You won!';
const PLAYER2_WON_MESSAGE = 'The computer won!';
const DRAW_MESSAGE = 'It\'s a draw!';
const UNKNOWN_WINNER_MESSAGE = 'I r confuse about who won!?';
const ARTIFICIAL_THINKING_TIME = 750;
const CROSS = 'X';
const NOUGHT = 'O';
const EMPTY = '-';

let cellElements;
let player1Piece = CROSS;
let player2Piece = NOUGHT;
let started = false;
let gameOver = false;
let computerMoveInProgress = false;

$(document).ready(() => {
    $('#startBtn').click(onStart);
    const cellIds = [
        '#cell00', '#cell01', '#cell02',
        '#cell10', '#cell11', '#cell12',
        '#cell20', '#cell21', '#cell22'
    ];
    cellElements = cellIds.map(id => $(id));
    cellElements.forEach(ce => ce.click(onCellClick));
    reset();
});

function reset() {
    updateBoardFromString(EMPTY.repeat(9));
    cellElements.forEach(ce => ce.removeClass('highlight'));
    started = false;
    gameOver = false;
    computerMoveInProgress = false;
    hideSpinner();
}

function onStart() {
    reset();
    started = true;
}

function onCellClick(e) {
    if (!started || gameOver || computerMoveInProgress) {
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

    computerMoveInProgress = true;

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
        .then(handleComputerMoveResponse)
        .catch(handleComputerMoveError)
        .always(() => { computerMoveInProgress = false; });
    }, ARTIFICIAL_THINKING_TIME);
}

function handleComputerMoveResponse(state) {
    updateBoardFromString(state.board);
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
    }, "");
}

function updateBoardFromString(board) {
    cellElements.forEach((ce, index) => {
        setCell(ce, board.charAt(index));
    });
}

function showSpinner() {
    $('#spinner').show();
}

function hideSpinner() {
    $('#spinner').hide();
}
